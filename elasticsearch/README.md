# elasticsearch-analysis-seunjeon
[seunjeon](https://bitbucket.org/eunjeon/seunjeon) 한국어 형태소분석기를 elasticsearch에 사용할 수 있도록 만든 plugin입니다.

## 설치
```bash
./bin/plugin install org.bitbucket.eunjeon/elasticsearch-analysis-seunjeon/2.1.0.0
```

## Release
| elasticsearch-analysis-seunjeon | Target elasticsearch version |
| ------------------------------- | ---------------------------- |
| 2.1.0.0                         | 2.1.0                        |

## 사용
```bash
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

```

## 옵션인자
| 옵션인자      | 설명               | 기본값 |
| ------------- | -----              | ---- |
| user_words    | 사용자 사전        | []     |
| decompound    | 복합명사 분해      | true |
| deinflect     | 활용어의 원형 추출 | true |
| index_eojeol  | 어절 추출     | true |
| index_poses   | 추출할 품사        | ["N","SL", "SH", "SN", "XR", "V", "UNK"] |

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