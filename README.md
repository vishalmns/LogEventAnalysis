# LogEventAnalysis

## Build Log Image

#### cd log
#### ./gradlew clean build
#### docker build -t log .

This creates a log service jar and build the docker image named log

#### cd ..

#### docker-compose up

This brings up :
1) Log serivce in port 8090
2) Kafka broker
3) zookeeper
4) juypter
5) spark master
6) Two worker nodes

#### localhost:8090/healthcheck 

To check log service is running fine.

#### localhost:8090/anomalythree

This is generate the log data to replicate anomaly

#### localhost:4444 

In which jupyter notebook is running.
Open anomaly detector file.
Run all the cells.
This starts the anomaly detector and would print out if any anamolies are present


