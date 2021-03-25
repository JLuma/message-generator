# Simple Kafka message generator
# Usage example
java -Dconfig.file=./config.properties -jar kafka-message-generator.jar

# Configuration
config.properties
```
sample-mode=true - enable/disable sample mode
kafka.topic=sample-topic - kafka topic to write messages
kafka.bootstrap.servers=broker:9092 - kafka brokers
message.gen.mode=SCHEMA_BASED - message generation mode; allowable values: SCHEMA_BASED, CONSTANT_FILE
message.gen.constant-file.message-file-path= - File with static constant message (Only for CONSTANT_FILE generation mode)
#message.gen.schema-based.schema-file-path= - File with dynamic message schema (Only for SCHEMA_BASED generation mode)

#kafka.send.throttling.delay.millis=0 - Delay in millis between sending messages to kafka
#kafka.messages.amount=10 - Total messages number to be sent
#kafka.threads= - Number of threads to be used to send messages to Kafka
```

## Sample mode
In sample mode messages will not send to Kafka, but one example message will prints to console and writes to disk instead.  
It's may be useful in situations when you want to validate what messages will be generated with specified schema file.  

## Message generation modes
### Constant file
In this mode same file (static) content will be used as message to send to Kafka

### Schema based
In this mode content for every message will be generated according to message generation schema

# Message generation schema
### Default message schema  
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
