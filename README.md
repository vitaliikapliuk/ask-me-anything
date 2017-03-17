# Ask Me Anything!
Tiny RESTful web service with the following business requirements:

Application must expose REST API endpoints for the following functionality:
ask question
list all accepted questions
list all accepted questions by country code
User should be able to ask question publicly by providing question text.

### Service must perform question validation according to the following rules and reject question if:
Question contains blacklisted words listed in a dictionary
N questions / second are asked from a single country (essentially we want to limit number of questions coming from a country in
a given timeframe)
Service must perform origin country resolution using the following web service and store country code together with the
question. Because networking is unreliable and services tend to fail, let's agree on default country code - "lv".

# How to prepare environment for start the project?
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


## Some features
1. StopWords list (/src/main/resources/stopwords.txt), you cannot ask question if it contain some from this list
2. Time Frame create questions rate by ISO code (for each country separatelly), you can change 'time_frame.max_iso_calls_per_second'
param in app.properties file (src/main/resources/app.properties), by default max 100 new questions per second for each country
3. ISO code by user IP address (freegeoip.net service for detection). If server is unavailable or access from localhost - then by default will be return 'LV'

## Some use cases
[ask question](http://localhost:7777/docs/#!/QuestionsService/create)

	{
		"questionText": "Hello, I am a question!"
	}

[list all accepted questions](http://localhost:7777/docs/#!/QuestionsService/getAll)

[list all accepted questions by country code](http://localhost:7777/docs/#!/QuestionsService/getAll) you may use 'countyIso' query param for search by ISO codes

[list all accepted questions by search](http://localhost:7777/docs/#!/QuestionsService/getAll) you may use 'search' query param for search by question text

### reject question if
Question contains blacklisted words listed in a dictionary (/src/main/resources/stopwords.txt)

N questions / second are asked from a single country (limit number of questions coming from a country in a given timeframe)

