# Team
Vakulenko Nataliia

# About the project
Language: Java

Input data set is a reddit csv file: 
- size is 104,9 mb
- records number is approximatelly 420 000

Components:
- Zookeeper
- Producer:
-- Java application
-- reads records from `train-balanced-sarcasm.csv` file and send them one by one to `reddit topic`
-- appends to the record `produced timestamp` for later metrics processing
- Kafka broker
- Consumer:
-- Java application
-- reads messages from kafka broker and saves metrics to mongodb
- Metrics:
-- Java application
-- builds reports based on metrics stored in mongodb by consumer
- Mongo DB:
-- used for storing metrics
- Kafdrop:
-- basic web user interface for Kafka
-- available on `localhost:9000`

# Scaling
- Scale producers: `docker-compose up --scale producer=2`
- Scale consumer: `docker-compose up --scale consumer=10`
- Scale broker: change property `KAFKA_PARTITIONS_COUNT` in producer configuration inside docker-compose file

# Running the application
- run `docker-compose build`
- run `docker-compose up -d`
- run metrics application: 
-- `cd ./metrics`
-- run `mvn compile exec:java`
