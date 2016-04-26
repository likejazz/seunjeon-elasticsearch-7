FILE_DATE=20160418
ZIP_FILE=kowiki-${FILE_DATE}-cirrussearch-content.json.gz
wget http://dumps.wikimedia.org/other/cirrussearch/${FILE_DATE}/${ZIP_FILE}
#curl "https://ko.wikipedia.org/w/api.php?action=cirrus-mapping-dump&format=json" > mapping.json
#jq .content < mapping.json | curl -XPUT localhost:9200/kowiki_content --data @mapping.json

curl -XDELETE localhost:9200/kowiki_content
sleep 5
curl -XPUT localhost:9200/kowiki_content -d '{
  "settings" : {
    "index":{
      "analysis":{
        "analyzer":{
          "default":{
            "type":"custom",
            "tokenizer":"seunjeon_tokenizer"
          }
        },
        "tokenizer": {
          "seunjeon_tokenizer": {
            "type": "seunjeon_tokenizer"
          }
        }
      }
    }
  }
}'

date
zcat kowiki-${FILE_DATE}-cirrussearch-content.json.gz | parallel --pipe -L 2 -N 2000 -j3 'curl -s http://localhost:9200/kowiki_content/_bulk --data-binary @- > /dev/null'
date
