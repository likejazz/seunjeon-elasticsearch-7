FILE_DATE=20160905
#WIKI_TYPE="kowikibooks"
#WIKI_TYPE="kowiktionary"
WIKI_TYPE="kowikinews"
INDEX_NAME=${WIKI_TYPE}_content
ZIP_FILE=${WIKI_TYPE}-${FILE_DATE}-cirrussearch-content.json.gz
if [ ! -f $ZIP_FILE ]; then
    wget http://dumps.wikimedia.org/other/cirrussearch/${FILE_DATE}/${ZIP_FILE}
fi

curl -XDELETE localhost:9200/${INDEX_NAME}
sleep 5

#curl "https://ko.wikipedia.org/w/api.php?action=cirrus-mapping-dump&format=json" > mapping.json
#jq .content < mapping.json | curl -XPUT localhost:9200/${INDEX_NAME} --data @mapping.json

curl -XPUT localhost:9200/${INDEX_NAME} -d '{
  "settings" : {
    "index":{
      "analysis":{
        "analyzer":{
          "default":{ "type":"custom", "tokenizer":"seunjeon_tokenizer" }
        },
        "tokenizer": {
          "seunjeon_tokenizer": { "type": "seunjeon_tokenizer" }
        }
      },
      "number_of_shards": 1,
      "number_of_replicas": 0
    }
  }
}'

sleep 5
curl -XPUT "localhost:9200/${INDEX_NAME}/_settings?pretty" -d '{
    "index" : {
        "refresh_interval" : -1
    }
}'

date
zcat ${ZIP_FILE} | parallel --pipe -L 2 -N 2000 -j3 "curl -s http://localhost:9200/${INDEX_NAME}/_bulk --data-binary @- > /dev/null"
date

