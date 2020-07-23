# Simple Kafka message generator
# Usage example:
### Minimal configuration  
java -Dkafka.bootstrap.servers=localhost:9092 -Dkafka.topic=pes -jar kafka-message-generator.jar  
### Full configuration  
java -Dkafka.bootstrap.servers=localhost:9092 -Dkafka.topic=pes -Dkafka.messages.amount=100 -Dkafka.threads=8 -Dmessage.schema.file.path=./my-schema.json -jar kafka-message-generator.jar

# Sample mode
In sample mode messages will not send to Kafka, but one example message will prints to console and writes to disk instead.  
It's may be useful in situations when you want to validate what messages will be generated with specified schema file.  
### Sample mode activation
java -Dsample-mode=true -jar kafka-message-generator.jar (default schema)  
java -Dsample-mode=true -Dmessage.schema.file.path=./my-schema.json -jar kafka-message-generator.jar (custom schema)  

# Message generation schema
### Default message schema  
```
{
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
