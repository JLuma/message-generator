package org.jluma.tools.loadtesting.syntheticdatagenerator.sender.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageSender;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.SenderProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "sender.type", havingValue = "http")
@RequiredArgsConstructor
public class HttpSender implements MessageSender {

    private static final TimeValue DEFAULT_KEEP_ALIVE = TimeValue.ofSeconds(30);
    private static final TimeValue IDLE_EVICTION_PERIOD = TimeValue.ofSeconds(15);
    private static final Timeout CONNECT_TIMEOUT = Timeout.ofSeconds(5);
    private static final int MAX_LOGGED_BODY_LENGTH = 1000;

    private final SenderProperties senderProperties;

    /** Один клиент на весь жизненный цикл отправителя. Закрывается только в close(). */
    private CloseableHttpClient httpClient;
    private HttpHost targetHost;
    private String requestPath;
    private String httpMethod;
    private Header[] requestHeaders;
    private String url;
    private volatile boolean initialized = false;

    @Override
    public synchronized void initialize() {
        if (initialized) {
            log.warn("HttpSender already initialized");
            return;
        }

        SenderProperties.HttpProps httpProps = senderProperties.getHttp();
        url = httpProps.getUrl();

        URI uri = parseUrl(url);
        // Порт -1 означает «по умолчанию для схемы»: клиент сам подставит 80 или 443
        targetHost = new HttpHost(uri.getScheme(), uri.getHost(), uri.getPort());
        requestPath = buildRequestPath(uri);
        httpMethod = resolveMethod(httpProps.getMethod());
        requestHeaders = buildHeaders(httpProps.getHeaders());
        httpClient = createHttpClient(httpProps);

        initialized = true;
        log.info("HttpSender initialized for URL: {} with method: {}", url, httpMethod);
    }

    @Override
    public void sendMessage(String message) {
        if (!initialized) {
            throw new IllegalStateException("HttpSender not initialized. Call initialize() first.");
        }

        CloseableHttpClient client = httpClient;
        ClassicHttpRequest request = buildRequest(message);
        try {
            // Вызов с обработчиком ответа: клиент сам дочитает тело, закроет ответ
            // и вернёт соединение в пул, даже если обработчик бросит исключение
            client.execute(targetHost, request, response -> {
                processResponse(response);
                return null;
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to send message via HTTP to " + url, e);
        }
    }

    @Override
    public synchronized void close() {
        initialized = false;
        if (httpClient != null) {
            httpClient.close(CloseMode.GRACEFUL);
        }
        log.info("HttpSender closed");
    }

    @Override
    public boolean isMultiThreadSupported() {
        return true;
    }

    // ---------------------------------------------------------------------
    // Построение запроса
    // ---------------------------------------------------------------------

    private ClassicHttpRequest buildRequest(String message) {
        String body = message != null ? message : "";

        ClassicHttpRequest request = switch (httpMethod) {
            case "GET" -> new HttpGet(appendMessageQuery(body));
            case "PUT" -> new HttpPut(requestPath);
            case "PATCH" -> new HttpPatch(requestPath);
            default -> new HttpPost(requestPath);
        };

        request.setHeaders(requestHeaders);
        if (!"GET".equals(httpMethod)) {
            request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        }
        return request;
    }

    /** Для GET сообщение передаётся параметром запроса message. */
    private String appendMessageQuery(String message) {
        if (message.isEmpty()) {
            return requestPath;
        }
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String separator = requestPath.contains("?") ? "&" : "?";
        return requestPath + separator + "message=" + encoded;
    }

    // ---------------------------------------------------------------------
    // Обработка ответа
    // ---------------------------------------------------------------------

    private void processResponse(ClassicHttpResponse response) throws IOException, ParseException {
        int statusCode = response.getCode();
        if (statusCode >= 200 && statusCode < 300) {
            // Тело успешного ответа дочитает и освободит сам execute()
            return;
        }

        HttpEntity entity = response.getEntity();
        String body = entity != null ? truncate(EntityUtils.toString(entity)) : "";

        if (statusCode >= 500) {
            log.error("HTTP server error {} from {}: {}", statusCode, url, body);
        } else {
            log.warn("HTTP error {} from {}: {}", statusCode, url, body);
        }
    }

    private static String truncate(String body) {
        if (body == null || body.length() <= MAX_LOGGED_BODY_LENGTH) {
            return body;
        }
        return body.substring(0, MAX_LOGGED_BODY_LENGTH) + "... (truncated, " + body.length() + " chars)";
    }

    // ---------------------------------------------------------------------
    // Разбор настроек
    // ---------------------------------------------------------------------

    private static URI parseUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("HTTP sender URL is not set");
        }

        URI uri;
        try {
            uri = new URI(url.trim());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }

        String scheme = uri.getScheme();
        if (scheme == null || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
            throw new IllegalArgumentException("URL must start with http:// or https://: " + url);
        }
        if (uri.getHost() == null) {
            throw new IllegalArgumentException("URL has no host: " + url);
        }
        return uri;
    }

    private static String buildRequestPath(URI uri) {
        String path = uri.getRawPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        String query = uri.getRawQuery();
        return query != null ? path + "?" + query : path;
    }

    private static String resolveMethod(String method) {
        if (method == null || method.isBlank()) {
            return "POST";
        }
        String normalized = method.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "POST", "PUT", "PATCH", "GET" -> normalized;
            default -> {
                log.warn("Unsupported HTTP method '{}', falling back to POST", method);
                yield "POST";
            }
        };
    }

    private static Header[] buildHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return new Header[0];
        }
        return headers.entrySet().stream()
                .map(e -> new BasicHeader(e.getKey(), e.getValue()))
                .toArray(Header[]::new);
    }

    // ---------------------------------------------------------------------
    // HTTP-клиент
    // ---------------------------------------------------------------------

    private static CloseableHttpClient createHttpClient(SenderProperties.HttpProps props) {
        Timeout socketTimeout = Timeout.ofMilliseconds(props.getSocketTimeoutMillis());

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(socketTimeout)
                .setTimeToLive(TimeValue.ofMilliseconds(props.getTimeToLiveMillis()))
                .build();

        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(createTrustAllSocketFactory())
                .setDefaultConnectionConfig(connectionConfig)
                .setMaxConnTotal(props.getMaxTotalConnections())
                .setMaxConnPerRoute(props.getDefaultMaxPerRoute())
                .build();

        // Сколько ждать свободное соединение из пула, прежде чем упасть с ошибкой
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(socketTimeout)
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy((response, context) -> DEFAULT_KEEP_ALIVE)
                .evictIdleConnections(IDLE_EVICTION_PERIOD)
                .evictExpiredConnections()
                .build();
    }

    /**
     * Доверяет любым сертификатам и не проверяет имя хоста.
     * Допустимо только для нагрузочного стенда, не для продакшна.
     */
    private static SSLConnectionSocketFactory createTrustAllSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            }, new SecureRandom());
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to create trust-all SSL context", e);
        }
    }
}