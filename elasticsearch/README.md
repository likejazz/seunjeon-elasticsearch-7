# elasticsearch-analysis-seunjeon
[seunjeon](https://bitbucket.org/eunjeon/seunjeon) 한국어 형태소분석기를 elasticsearch에 사용할 수 있도록 만든 plugin입니다.

## 설치
```bash
./bin/plugin install org.bitbucket.eunjeon/elasticsearch-analysis-seunjeon/2.1.1.3
```

## Release
| elasticsearch-analysis-seunjeon | Target elasticsearch version | release note |
| ------------------------------- | ---------------------------- | - |
| 2.1.1.3                         | 2.1.1                        | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.1.3) |
| 2.1.1.2                         | 2.1.1                        | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.1.2) |
| 2.1.1.1                         | 2.1.1                        | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.1.1) |
| 2.1.1.0                         | 2.1.1                        | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.1.0) |
| 2.1.0.0                         | 2.1.0                        | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.0.0) |

## 사용
```bash
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
            "user_words": ["낄끼빠빠,-100", "버카충", "abc마트"]
          }
        }
      }
    }
  }
}'

sleep 1

echo "========================================================================"
curl -XGET "${ES}/${ESIDX}/_analyze?analyzer=korean&pretty" -d '삼성전자'
echo "========================================================================"
curl -XGET "${ES}/${ESIDX}/_analyze?analyzer=korean&pretty" -d '빨라짐'
echo "========================================================================"
curl -XGET "${ES}/${ESIDX}/_analyze?analyzer=korean&pretty" -d '낄끼빠빠 어그로'
```

## 옵션인자
| 옵션인자      | 설명               | 기본값 |
| ------------- | -----           | ---- |
| user_words    | 사용자 사전        | []     |
| user_dict_path| 사용자 사전 파일. base directory는 ES_HOME/config 입니다. |      |
| decompound    | 복합명사 분해      | true |
| deinflect     | 활용어의 원형 추출 | true |
| index_eojeol  | 어절 추출     | true |
| index_poses   | 추출할 품사        | ["N","SL", "SH", "SN", "XR", "V", "UNK"] |
| pos_tagging   | 품사태깅. 키워드에 품사를 붙여서 토큰을 뽑습니다        | true |

* 사용사 사전은 하나만 관리하기 떄문에 여러개의 tokenizer를 생성하여도 마지막 로드된 사전만 유지됩니다.
* user_words와 user_dict_path 를 함께 설정할 경우 user_words 는 무시되고 user_dict_path만 적용됩니다.
* `"pos_tagging": true` 의 경우 키워드와 품사가 함께 토큰(ex:`자전거/N`)으로 나오기 때문에 stopword filter나 synonym filter 사용시 적용이 안될 수 있습니다. `"pos_tagging": false`로 설정을 하여 사용하거나, filter사전을 `자전거/N`의 형태로 만들어야 합니다.


### 품사태그표
| 품사 태그 | 설명 |
| --- | --- |
| UNK | 미지어 |
| EP  | 선어말어미 |
| E   | 어미 |
| I   | 독립언 |
| J   | 관계언 |
| M   | 수식언 |
| N   | 체언 |
| S   | 부호 |
| SL  | 외국어 |
| SH  | 한자 |
| SN  | 숫자 |
| V   | 용언 |
| VCP | 긍정지정사 |
| XP  | 접두사 |
| XS  | 접미사 |
| XR  | 어근 |


## elasticsearch 플러그인 개발
```bash
# 사전 다운로드
./scripts/download-dict.sh mecab-ko-dic-2.0.1-20150920

# 사전 빌드(mecab-ko-dic/* -> src/main/resources/*.dat)
sbt -J-Xmx2G "run-main org.bitbucket.eunjeon.seunjeon.DictBuilder"

# zip 생성
sbt
> project elasticsearch
> esZip
```
## License
Copyright 2015 유영호, 이용운. 아파치 라이센스 2.0에 따라 소프트웨어를 사용, 재배포 할 수 있습니다. 더 자세한 사항은 http://www.apache.org/licenses/LICENSE-2.0 을 참조하시기 바랍니다.