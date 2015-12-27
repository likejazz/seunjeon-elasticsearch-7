#!/bin/bash

ES='http://localhost:9200'
ESIDX='seunjeon'

curl -XDELETE $ES/$ESIDX?pretty

curl -XPUT $ES/$ESIDX/?pretty -d '{
  "settings" : {
    "index":{
      "analysis":{
        "analyzer":{
          "korean":{
            "type":"custom",
            "tokenizer":"seunjeon_tokenizer"
          }
        }
      }
    }
  }
}'

sleep 1
curl -XGET $ES/$ESIDX/_analyze?analyzer=korean\&pretty -d '은전한닢 프로젝트'
