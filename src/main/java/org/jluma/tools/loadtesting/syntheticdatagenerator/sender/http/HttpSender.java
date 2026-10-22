package org.jluma.tools.loadtesting.syntheticdatagenerator.sender.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.TimeValue;
import org.jluma.tools.loadtesting.syntheticdatagenerator.api.MessageSender;
import org.jluma.tools.loadtesting.syntheticdatagenerator.config.SenderProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConditionalOnProperty(name = "sender.type", havingValue = "http")
@RequiredArgsConstructor
public class HttpSender implements MessageSender {

    private static final int ASYNC_THREADS = 20;
    private static final TimeValue DEFAULT_KEEP_ALIVE = TimeValue.ofSeconds(30);

    private final SenderProperties senderProperties;

    private HttpClient httpClient;
    private ExecutorService executorService;
    private HttpHost targetHost;
    private URI targetUri;
    private String httpMethod;
    private Header[] requestHeaders;
    private volatile boolean initialized = false;

    @Override
    public void initialize() {
        if (initialized) {
            log.warn("HttpSender already initialized");
            return;
        }

        try {
            SenderProperties.HttpProps httpProps = senderProperties.getHttp();
            String url = httpProps.getUrl();

            // Parse URL to extract scheme, host, port, path
            int schemeEnd = url.indexOf("://");
            if (schemeEnd == -1) {
                throw new IllegalArgumentException("Invalid URL: " + url);
            }
            String scheme = url.substring(0, schemeEnd);
            String remainder = url.substring(schemeEnd + 3);

            int hostEnd = remainder.indexOf("/");
            String authority;
            String path;
            if (hostEnd != -1) {
                authority = remainder.substring(0, hostEnd);
                path = remainder.substring(hostEnd);
            } else {
                authority = remainder;
                path = "/";
            }

            int portIdx = authority.indexOf(":");
            int port;
            String host;
            if (portIdx != -1) {
                host = authority.substring(0, portIdx);
                port = Integer.parseInt(authority.substring(portIdx + 1));
            } else {
                host = authority;
                port = "https".equalsIgnoreCase(scheme) ? 443 : 80;
            }

            targetHost = new HttpHost(scheme, host, port);
            targetUri = URI.create(path);
            httpMethod = httpProps.getMethod().toUpperCase();

            // Build request headers
            Map<String, String> headers = httpProps.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                requestHeaders = headers.entrySet().stream()
                        .map(e -> new BasicHeader(e.getKey(), e.getValue()))
                        .toArray(Header[]::new);
            } else {
                requestHeaders = new Header[0];
            }

            // Create connection pool and HTTP client
            httpClient = createHttpClient();

            // Initialize async execution service for handling HTTP requests
            executorService = Executors.newFixedThreadPool(ASYNC_THREADS);

            initialized = true;
            log.info("HttpSender initialized for URL: {} with method: {}", url, httpMethod);
        } catch (Exception e) {
            log.error("Failed to initialize HttpSender", e);
            throw new RuntimeException("Failed to initialize HttpSender", e);
        }
    }

    @Override
    public void sendMessage(String message) {
        if (!initialized) {
            throw new IllegalStateException("HttpSender not initialized. Call initialize() first.");
        }

        try {
            // Use async execution for better throughput under high RPS
            Future<?> future = executorService.submit(() -> sendRequest(message));
            // Wait for completion with timeout
            future.get(senderProperties.getHttp().getSocketTimeoutMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Failed to send message via HTTP", e);
            throw new RuntimeException("Failed to send message via HTTP", e);
        }
    }

    private void sendRequest(String message) {
        try {
            switch (httpMethod) {
                case "POST" -> executePost(message);
                case "PUT" -> executePut(message);
                case "PATCH" -> executePatch(message);
                case "GET" -> executeGet(message);
                default -> executePost(message);
            }
        } catch (Exception e) {
            log.error("HTTP request failed for URL: {}", senderProperties.getHttp().getUrl(), e);
            throw new RuntimeException("HTTP request failed", e);
        }
    }

    private void executePost(String message) throws Exception {
        HttpPost httpRequest = new HttpPost(targetUri);
        httpRequest.setHeaders(requestHeaders);
        httpRequest.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));
        executeRequest(httpRequest);
    }

    private void executePut(String message) throws Exception {
        HttpPut httpRequest = new HttpPut(targetUri);
        httpRequest.setHeaders(requestHeaders);
        httpRequest.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));
        executeRequest(httpRequest);
    }

    private void executePatch(String message) throws Exception {
        HttpPatch httpRequest = new HttpPatch(targetUri);
        httpRequest.setHeaders(requestHeaders);
        httpRequest.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));
        executeRequest(httpRequest);
    }

    private void executeGet(String message) throws Exception {
        URI uri = targetUri;
        // For GET, append message as query parameter if needed
        if (message != null && !message.isEmpty()) {
            String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
            String existingQuery = targetUri.getQuery();
            String newQuery = existingQuery != null ? existingQuery + "&message=" + encoded : "message=" + encoded;
            uri = new URI(
                    targetUri.getScheme(),
                    targetUri.getAuthority(),
                    targetUri.getPath(),
                    newQuery,
                    targetUri.getFragment()
            );
        }

        HttpGet httpRequest = new HttpGet(uri);
        httpRequest.setHeaders(requestHeaders);
        executeRequest(httpRequest);
    }

    private void executeRequest(ClassicHttpRequest httpRequest) throws Exception {
        try (CloseableHttpClient client = (CloseableHttpClient) httpClient) {
            org.apache.hc.core5.http.ClassicHttpResponse response =
                    client.execute(targetHost, httpRequest, new BasicHttpContext());
            processResponse(response);
        }
    }

    private void processResponse(org.apache.hc.core5.http.ClassicHttpResponse response) throws Exception {
        int statusCode = response.getCode();
        HttpEntity entity = response.getEntity();

        if (statusCode >= 200 && statusCode < 300) {
            // Success - don't consume entity to save resources
        } else if (entity != null) {
            String responseBody = EntityUtils.toString(entity);
            if (statusCode >= 500) {
                log.error("HTTP server error {} from {}: {}", statusCode, senderProperties.getHttp().getUrl(), responseBody);
            } else {
                log.warn("HTTP client error {} from {}: {}", statusCode, senderProperties.getHttp().getUrl(), responseBody);
            }
            EntityUtils.consume(entity);
        } else {
            if (statusCode >= 500) {
                log.error("HTTP server error {} from {}", statusCode, senderProperties.getHttp().getUrl());
            } else {
                log.warn("HTTP client error {} from {}", statusCode, senderProperties.getHttp().getUrl());
            }
        }
    }

    private HttpClient createHttpClient() {
        try {
            // Create trust-all SSL context for simplicity
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new javax.net.ssl.TrustManager[]{
                    new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
            }, new java.security.SecureRandom());

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

            // Build connection pool manager - key for high RPS performance
            ConnectionConfig defaultConfig = ConnectionConfig.custom()
                    .setTimeToLive(TimeValue.ofMilliseconds(senderProperties.getHttp().getTimeToLiveMillis()))
                    .build();

            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .setDefaultConnectionConfig(defaultConfig)
                    .setMaxConnTotal(senderProperties.getHttp().getMaxTotalConnections())
                    .setMaxConnPerRoute(senderProperties.getHttp().getDefaultMaxPerRoute())
                    .build();

            // Create the HTTP client with proper timeouts and connection management
            return HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .setConnectionManagerShared(false)
                    .setKeepAliveStrategy((response, context) -> DEFAULT_KEEP_ALIVE)
                    .evictIdleConnections(TimeValue.ofSeconds(15))
                    .evictExpiredConnections()
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HttpClient", e);
        }
    }

    @Override
    public synchronized void close() {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }

            if (httpClient instanceof CloseableHttpClient) {
                ((CloseableHttpClient) httpClient).close(CloseMode.GRACEFUL);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (httpClient instanceof CloseableHttpClient) {
                ((CloseableHttpClient) httpClient).close(CloseMode.GRACEFUL);
            }
        }
        initialized = false;
        log.info("HttpSender closed");
    }

    @Override
    public boolean isMultiThreadSupported() {
        return true;
    }

}
