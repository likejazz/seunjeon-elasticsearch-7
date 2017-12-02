#!/usr/bin/env bash

ES='http://localhost:9200'
ESIDX='seunjeon-idx'
CURL="curl --silent -H Content-Type:application/json"

function assertEquals() {
    local actual=$1
    local expect=$2

    if [ "$expect" == "$actual" ]; then
        return 0
    else
        echo "fail: expect: $expect, but actual: $actual"
        command -v jq > /dev/null
        if [ $? -eq 0 ]; then
            echo "expect:"
            echo $expect | jq
            echo "actual:"
            echo $actual | jq
        fi
        return 1
    fi
}

function test_analysis() {
    local input=$1
    local expect=$2

    RESULT=$($CURL -XGET "${ES}/${es_idx}/_analyze" -d "{
        \"analyzer\": \"korean\", 
        \"text\": \"$input\"
    }") 
    assertEquals $RESULT "$expect"
    if [ "$?" -eq 0 ]; then
        echo "success $input"
    fi
    
}

function testSeunjeon1 {
    local es_idx="seunjeon-idx"
    local settings='
{
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
            "user_words": ["낄끼+빠빠,-100", "c\\+\\+", "어그로", "버카충", "abc마트"]
          }
        }
      }
    }
  },
  "mappings": {
    "doc": {
      "properties": {
        "field1": { "type": "text", "analyzer": "korean" }
      }
    }
  }
}
'

    curl -XDELETE "${ES}/${es_idx}?pretty"
    sleep 1
    $CURL -XPUT "${ES}/${es_idx}/?pretty" -d "$settings"

    sleep 1

    test_analysis "삼성전자" '{"tokens":[{"token":"삼성/N","start_offset":0,"end_offset":2,"type":"N","position":0},{"token":"전자/N","start_offset":2,"end_offset":4,"type":"N","position":1}]}'

    test_analysis "빨라짐" '{"tokens":[{"token":"빠르/V","start_offset":0,"end_offset":2,"type":"V","position":0},{"token":"지/V","start_offset":2,"end_offset":3,"type":"V","position":1}]}'

    test_analysis "슬픈" '{"tokens":[{"token":"슬프/V","start_offset":0,"end_offset":2,"type":"V","position":0}]}'

    test_analysis "새로운사전생성" '{"tokens":[{"token":"새롭/V","start_offset":0,"end_offset":2,"type":"V","position":0},{"token":"사전/N","start_offset":3,"end_offset":5,"type":"N","position":1},{"token":"생성/N","start_offset":5,"end_offset":7,"type":"N","position":2}]}'

    test_analysis "낄끼빠빠 c++" '{"tokens":[{"token":"낄끼/N","start_offset":0,"end_offset":2,"type":"N","position":0},{"token":"빠빠/N","start_offset":2,"end_offset":4,"type":"N","position":1},{"token":"c++/N","start_offset":5,"end_offset":8,"type":"N","position":2}]}'

    $CURL -XPOST "${ES}/${es_idx}/doc/1?pretty" -d ' { "field1" : "삼성전자" }' | jq
    $CURL -XGET "${ES}/${es_idx}/doc/1" | jq

    $CURL -XPOST "${ES}/${es_idx}/doc/2?pretty" -d ' { "field1" : ["삼성 전자", "엘지 전자"] }' | jq
    $CURL -XGET "${ES}/${es_idx}/doc/2" | jq

    $CURL -XGET "${ES}/${es_idx}/_analyze" -d "{
        \"analyzer\": \"korean\", 
        \"text\": [\"삼성전자\", \"엘지전자\"]
    }" | jq

    $CURL -XGET "${ES}/${es_idx}/_analyze" -d "{
        \"analyzer\": \"korean\", 
        \"text\": \"세부막탄 5일_[Diet상품]비 리조트 | 객실업그레이드+발마사지\"
    }" | jq
}

testSeunjeon1


function testSeunjeon2 {
    local es_idx="seunjeon-idx"
    local settings='
{
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
            "index_eojeol": true,
            "user_words": ["낄끼+빠빠,-100", "c\\+\\+", "어그로", "버카충", "abc마트"]
          }
        }
      }
    }
  },
  "mappings": {
    "doc": {
      "properties": {
        "field1": { "type": "text", "analyzer": "korean" }
      }
    }
  }
}
'

    curl -XDELETE "${ES}/${es_idx}?pretty"
    sleep 1
    $CURL -XPUT "${ES}/${es_idx}/?pretty" -d "$settings"

    sleep 1

    test_analysis "낄끼빠빠 c++" '{"tokens":[{"token":"낄끼/N","start_offset":0,"end_offset":2,"type":"N","position":0},{"token":"낄끼빠빠/EOJ","start_offset":0,"end_offset":4,"type":"EOJ","position":0,"positionLength":2},{"token":"빠빠/N","start_offset":2,"end_offset":4,"type":"N","position":1},{"token":"c++/N","start_offset":5,"end_offset":8,"type":"N","position":2}]}'

    $CURL -XGET "${ES}/${es_idx}/_analyze" -d "{
        \"analyzer\": \"korean\", 
        \"text\": \"점쟁이 문어 파울, 스페인의 FIFA 월드컵 우승 예언\"
    }" | jq

    $CURL -XGET "${ES}/${es_idx}/_analyze" -d "{
        \"analyzer\": \"korean\", 
        \"text\": \"비 리조트\"
    }" | jq

    $CURL -XPOST "${ES}/${es_idx}/doc/1?pretty" -d ' { "field1" : "비 리조트" }' | jq
    $CURL -XGET "${ES}/${es_idx}/doc/1" | jq

    $CURL -XPOST "${ES}/${es_idx}/doc/1?pretty" -d ' { "field1" : "점쟁이 문어 파울, 스페인의 FIFA 월드컵 우승 예언" }' | jq
    $CURL -XGET "${ES}/${es_idx}/doc/1" | jq

}

testSeunjeon2
