# Simple Kafka message generator
# Usage example
java -Dconfig.file=./config.properties -jar kafka-message-generator.jar

# Configuration (config.properties)
Property name | Allowable values/type | Description | Default value
--- | --- | --- | ---
sample-mode | boolean | Enable/Disable sample mode (Described in detail below) | false
message.gen.mode | SCHEMA_BASED, CONSTANT_FILE | Message generation mode | SCHEMA_BASED
message.gen.constant-file.message-file-path | String | Path to file, which be used as constant/static message (Works with message generation mode CONSTANT_FILE only)  | -
message.gen.schema-based.schema-file-path | String | Path to file, which be used as dynamic message schema (Works with message generation mode SCHEMA_BASED only) | Default schema described below
kafka.topic | String | Kafka topic to write messages | -
kafka.bootstrap.servers | String | Comma separated kafka brokers list | -
kafka.send.throttling.delay.millis | Integer | Delay in millis between sending messages to kafka | 0
kafka.messages.amount | Integer | Total messages number to be sent | 10
kafka.threads | Integer | Number of threads to be used to send messages to Kafka | Runtime.getRuntime().availableProcessors()

## Sample mode
In sample mode messages will not send to Kafka, but one example message will prints to console and writes to disk instead.  
It's may be useful in situations when you want to validate what messages will be generated with specified schema file.  

## Message generation modes
### Constant file
In this mode same file (static) content will be used as message to send to Kafka

### Schema based
In this mode content for every message will be generated according to message generation schema

# Message generation schema
### Default dynamic message schema  
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
### Another simple dynamic message schema example
```
{
  "time": {
    "type": "incrementing_time"
  },
  "organization": {
    "type": "const",
    "value": "Dog"
  },
  "id": {
    "type": "random_int"
  },
  "name": {
    "type": "vocabulary",
    "vocabulary": ["Pes", "Peska", "Pesinessa", "Pesiel", "Psinka"]
  },
  "login": {
    "type": "random_string"
  },
  "characteristic": {
    "type": "vocabulary_text",
    "vocabulary": ["smart", "nice", "toxic", "angry", "good boy", "kind", "really pes"]
  },
  "about self": {
    "type": "random_text"
  }
}
```

