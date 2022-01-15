FROM docker.elastic.co/elasticsearch/elasticsearch:7.9.1

RUN bin/elasticsearch-plugin install analysis-nori
# COPY ./analysis-seunjeon-7.9.1.zip /usr/share/elasticsearch/
# RUN bin/elasticsearch-plugin install file://`pwd`/analysis-seunjeon-7.9.1.zip
RUN bin/elasticsearch-plugin install https://github.com/likejazz/seunjeon-elasticsearch-7/releases/download/7.9.1/analysis-seunjeon-7.9.1.zip
