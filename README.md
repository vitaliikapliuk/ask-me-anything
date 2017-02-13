#How to prepare environment for start the project?
1. Java 8 SDK
2. Install [Maven](https://maven.apache.org/)
3. Install [ElasticSearch](https://www.elastic.co/downloads/elasticsearch) (was tested on 5.2.0)
 (also you can use the command for install and run ElasticSearch 5.2.0):

 wget -qO- -O tmp.zip https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.2.0.zip && unzip tmp.zip && rm tmp.zip && cd elasticsearch-5.2.0/bin/ && ./elasticsearch

you can check elasticsearch status by link http://localhost:9200/ (must return some technical info)

#How to run the project?
1. Clone the project
2. start building process
	mvn clean install
3. Run
	java -jar target/ask-me-anything-1.0.jar
4. After finishing building process you can have access to documentation of the project:
	http://127.0.0.1:7777/docs/
