FROM docker.elastic.co/elasticsearch/elasticsearch:7.16.3

RUN bin/elasticsearch-plugin install analysis-nori
RUN bin/elasticsearch-plugin install https://github.com/likejazz/seunjeon-elasticsearch-7/releases/download/7.16.3/analysis-seunjeon-7.16.3.zip
