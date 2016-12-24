#!/usr/bin/env bash

ES='http://localhost:9200'
ESIDX='seunjeon-idx'

curl -XDELETE "${ES}/${ESIDX}?pretty"
sleep 1
curl -XPUT "${ES}/${ESIDX}/?pretty" -d '{
  "settings" : {
    "index":{
      "analysis":{
        "analyzer":{
          "korean":{
            "type":"custom",
            "tokenizer":"seunjeon_default_tokenizer"
          }
        },
        "tokenizer": {
          "seunjeon_default_tokenizer": {
            "type": "seunjeon_tokenizer",
            "index_eojeol": false,
            "user_dict_path": "user_dict.csv"
          }
        }
      }
    }
  }
}'

sleep 1

echo "# 삼성/N 전자/N"
curl -XGET "${ES}/${ESIDX}/_analyze?analyzer=korean&pretty" -d '삼성전자'

echo "# 빠르/V 지/V"
curl -XGET "${ES}/${ESIDX}/_analyze?analyzer=korean&pretty" -d '빨라짐'

echo "# 슬프/V"
curl -XGET "${ES}/${ESIDX}/_analyze?analyzer=korean&pretty" -d '슬픈'

echo "# 새롭/V 사전/N 생성/N"
curl -XGET "${ES}/${ESIDX}/_analyze?analyzer=korean&pretty" -d '새로운사전생성'

echo "# 낄끼/N 빠빠/N c++/N"
curl -XGET "${ES}/${ESIDX}/_analyze?analyzer=korean&pretty" -d '낄끼빠빠 c++'
