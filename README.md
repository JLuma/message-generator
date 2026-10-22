# Message Generator (Spring Boot version)

Spring Boot application for generating synthetic messages and sending them to Kafka.

## Quick Start

### Build
```bash
mvn clean package
```

### Run with default configuration
```bash
java -jar target/message-generator-1.0.0.jar
```

### Run with custom configuration file
```bash
java -jar target/message-generator-1.0.0.jar --spring.config.additional-location=file:./config.yml
```

## Configuration

All configuration is done via `application.yml` or environment variables.

### Sample Configuration

```yaml
# Message generator settings
message:
  generator:
    # Enable sample mode - generates one example message
    sample-mode: false
    # Message generation mode: SCHEMA_BASED or CONSTANT_FILE
    mode: SCHEMA_BASED
    constant-file:
      message-file-path:  # Path to constant message file
    schema-based:
      schema-file-path:   # Path to JSON schema file (uses default-schema.json if not set)

# Kafka settings
kafka:
  topic: messages
  bootstrap-servers: localhost:9092
  messages:
    amount: 100
  send:
    throttling:
      delay:
        millis: 0
  threads: 4
  client-id: KafkaMessageGenerator
```

### Configuration Properties

| Property | Type | Description | Default |
|----------|------|-------------|---------|
| `message.generator.sample-mode` | boolean | Enable sample mode | `false` |
| `message.generator.mode` | SCHEMA_BASED, CONSTANT_FILE | Message generation mode | `SCHEMA_BASED` |
| `message.generator.constant-file.message-file-path` | String | Path to constant message file | - |
| `message.generator.schema-based.schema-file-path` | String | Path to JSON schema file | `default-schema.json` |
| `kafka.topic` | String | Kafka topic | `messages` |
| `kafka.bootstrap-servers` | String | Kafka brokers | `localhost:9092` |
| `kafka.messages.amount` | Long | Total messages to send | `10` |
| `kafka.send.throttling.delay.millis` | Long | Delay between messages (ms) | `0` |
| `kafka.threads` | Integer | Number of sender threads | CPU cores |

## Modes

### Sample Mode
When `sample-mode: true`, the application generates one example message, prints it to console, and writes it to `./sample.json`.

### Message Generation Modes

#### Schema Based (default)
Generates dynamic messages according to a JSON schema. Each message is created by combining field values defined in the schema.

#### Constant File
Uses a static message from a file for all generated messages.

## Message Schema

When using `SCHEMA_BASED` mode, define your schema as a JSON file:

```
{
  "time": {
    "type": "incrementing_time",
    "dateFormat":"yyyy-MM-dd'T'HH:mm:ss.SSS",
    "dateIncrement": 30,
    "dateIncrementDelta": 10,
    "dateIncrementUnit": "SECONDS"
  },
  "organization": {
    "type": "const",
    "value": "OAO PES"
  },
  "id": {
    "type": "random_int"
  },
  "personnel number": {
    "type": "random_int",
    "minValue": 10000,
    "maxValue": 99999
  },
  "name": {
    "type": "vocabulary",
    "vocabulary": ["Pes", "Peska", "Pesinessa", "Pesiel", "Psinka"]
  },
  "team": {
    "type": "random_string",
    "minStringLength": 3,
    "maxStringLength": 10
  },
  "login": {
    "type": "random_string"
  },
  "characteristic": {
    "type": "vocabulary_text",
    "vocabulary": ["smart", "nice", "toxic", "angry", "good boy", "kind", "really pes"],
    "wordsDelimiter": ", ",
    "minTextWordsAmount": 2,
    "maxTextWordsAmount": 4
  },
  "about self": {
    "type": "random_text",
    "minWordLength": 10,
    "maxWordLength": 13,
    "minTextWordsAmount": 5,
    "maxTextWordsAmount": 15
  }
}

```
