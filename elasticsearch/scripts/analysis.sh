#!/bin/bash

ES='http://localhost:9200'
ESIDX='seunjeon-idx'

curl -XDELETE $ES/$ESIDX?pretty

curl -XPUT $ES/$ESIDX/?pretty -d '{
  "settings" : {
    "index":{
      "analysis":{
        "analyzer":{
          "korean":{
            "type":"custom",
            "tokenizer":"seunjeon_tokenizer"
          },
          "korean_noun": {
            "type":"custom",
            "tokenizer":"noun_tokenizer"
          }
        },
        "tokenizer": {
          "seunjeon_tokenizer": {
            "type": "seunjeon_tokenizer",
            "user_words": ["낄끼빠빠,-100", "버카충"]
          },
          "noun_tokenizer": {
            "type": "seunjeon_tokenizer",
            "index_eojeol": false,
            "index_poses": ["N"]
          }
        }
      }
    }
  }
}'

sleep 1
echo "========================================================================"
curl -XGET $ES/$ESIDX/_analyze?analyzer=korean\&pretty -d '낄끼빠빠'
echo "========================================================================"
curl -XGET $ES/$ESIDX/_analyze?analyzer=korean\&pretty -d '삼성전자'
echo "========================================================================"
curl -XGET $ES/$ESIDX/_analyze?analyzer=korean\&pretty -d '슬픈'
echo "========================================================================"
curl -XGET $ES/$ESIDX/_analyze?analyzer=korean_noun\&pretty -d '꽃이피다'
