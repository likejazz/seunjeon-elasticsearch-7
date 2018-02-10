ES=localhost:9200
INDEX_NAME=kowikiquote

curl -XDELETE $ES/${INDEX_NAME}
sleep 5

curl -XPUT $ES/${INDEX_NAME} -H 'Content-Type: application/json' -d '{
  "settings" : {
    "index":{
      "analysis":{
        "analyzer":{
          "default":{ "type":"custom", "tokenizer":"seunjeon_tokenizer" }
        },
        "tokenizer": {
          "seunjeon_tokenizer": { 
            "type": "seunjeon_tokenizer",
            "compress": true
          }
        }
      },
      "number_of_shards": 1,
      "number_of_replicas": 0
    }
  }
}'

pushd chunks

for file in *; do
  echo -n "${file}:  "
  took=$(curl -s -H 'Content-Type: application/x-ndjson' -XPOST $ES/$INDEX_NAME/_bulk?pretty --data-binary @$file | grep took | cut -d':' -f 2 | cut -d',' -f 1)
  printf '%7s\n' $took
  [ "x$took" = "x" ] || rm $file
done

popd
