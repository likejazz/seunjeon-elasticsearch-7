# elasticsearch-analysis-seunjeon
[seunjeon](https://bitbucket.org/eunjeon/seunjeon) 한국어 형태소분석기를 elasticsearch에 사용할 수 있도록 만든 plugin입니다.

## 설치
### elasticsearch 5.0.0 이상
```bash
# download plugin
bash <(curl -s https://bitbucket.org/eunjeon/seunjeon/raw/master/elasticsearch/scripts/downloader.sh) -e <es-version> -p <plugin-version>
  에제) $ bash <(curl -s https://bitbucket.org/eunjeon/seunjeon/raw/master/elasticsearch/scripts/downloader.sh) -e 6.1.1 -p 6.1.1.0

# install plugin
./bin/elasticsearch-plugin install file://`pwd`/elasticsearch-analysis-seunjeon-<plugin-version>.zip
  예제) $ ./bin/elasticsearch-plugin install file://`pwd`/elasticsearch-analysis-seunjeon-6.1.1.0.zip
```
  * downloader.sh 가 하는 일은 elasticsearch-analysis-seunjeon-<plugin-version>.zip 파일을 내려받은 후 plugin-descriptor.properties 의 elasticsearch.version 을 변경하여 재압축합니다.
  * elasticsearch가 버전 업 될때마다 플러그인을 재배포하는데 어려움이 있어 스크립트를 제공합니다.
  * 다운로드 받는데 문제가 있다면 최신버전을 직접 다운받으세요. https://oss.sonatype.org/service/local/repositories/releases/content/org/bitbucket/eunjeon/elasticsearch-analysis-seunjeon/6.1.1.0/elasticsearch-analysis-seunjeon-6.1.1.0.zip

### elasticsearch 2.4.1 이하
```bash
$ ./bin/elasticsearch-plugin install org.bitbucket.eunjeon/elasticsearch-analysis-seunjeon/2.6.1.1
```

## Release
| elasticsearch-analysis-seunjeon | target elasticsearch | release note |
| ------------------------------- | ---------------------| ------------ |
| 6.1.1.1                         | 6.1.1                | heap memory 사용 최적화 |
| 6.1.1.0                         | 6.1.1                | es 버전업으로인한 패치 |
| 6.0.0.1                         | 6.0.1                | 어절 position 오류 수정 |
| 6.0.0.0                         | 6.0.0                | list 필드 형태소분석시 offset 오류로 es6.0에서 에러나서 죽던 문제 해결 |
| 5.4.1.1                         | 5.4.1                | 사전 로딩 오류로 오분석 되던 문제 수정<br/>한자 SY로 분석되는 오류 수정 |
| 5.4.1.0                         | 5.4.1                | 사용자사전 인덱스별로 별도 로딩(시스템 사전만 singleton) |
| 5.1.1.1                         | 5.1.1                | 사용자 사전에 복합명사 등록 기능 추가  |
| 5.1.1.0                         | 5.1.1                | 추가 기능 없음 |
| 5.0.0.0                         | 5.0.0                | 추가 기능 없음 |
| 2.4.0.1                         | 2.4.0                | |
| 2.4.0.0                         | 2.4.0                | |
| 2.3.5.0                         | 2.3.5                | |
| 2.3.3.0                         | 2.3.3                | |
| 2.3.2.1                         | 2.3.2                | |
| 2.3.2.0                         | 2.3.2                | |
| 2.3.1.0                         | 2.3.1                | |
| 2.3.0.0                         | 2.3.0                | |
| 2.2.1.0                         | 2.2.1                | |
| 2.2.0.1                         | 2.2.0                | |
| 2.2.0.0                         | 2.2.0                | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.2.0.0) |
| 2.1.1.3                         | 2.1.1                | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.1.3) |
| 2.1.1.2                         | 2.1.1                | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.1.2) |
| 2.1.1.1                         | 2.1.1                | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.1.1) |
| 2.1.1.0                         | 2.1.1                | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.1.0) |
| 2.1.0.0                         | 2.1.0                | [note](http://eunjeon.blogspot.kr/search?q=elasticsearch-analysis-seunjeon+2.1.0.0) |


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
            "index_eojeol": false,
            "user_words": ["낄끼+빠빠,-100", "c\\+\\+", "어그로", "버카충", "abc마트"]
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
```

## 옵션인자
| 옵션인자      | 설명  | 기본값 |
| ------------- | ----- | ---- |
| user_words    | 사용자 사전        | []     |
| user_dict_path| 사용자 사전 파일. base directory는 ES_HOME/config 입니다. 사전파일 예제는 [user_dict.csv](https://bitbucket.org/eunjeon/seunjeon/raw/master/elasticsearch/scripts/user_dict.csv)를 참고하세요. |      |
| decompound    | 복합명사 분해      | true |
| deinflect     | 활용어의 원형 추출 | true |
| index_eojeol  | 어절 추출     | true |
| index_poses   | 추출할 품사        | ["N", "SL", "SH", "SN", "XR", "V", "M", "UNK"] |
| pos_tagging   | 품사태깅. 키워드에 품사를 붙여서 토큰을 뽑습니다        | true |
| max_unk_length  | unknown 키워드로 뽑을 수 있는 최대 길이(한글) | 8 |

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
  * [mecab-ko-dic 2.0 품사태그표](https://docs.google.com/spreadsheets/d/1-9blXKjtjeKZqsf4NzHeYJCrr49-nXeRF6D80udfcwY/edit?usp=sharing)

## java options
| 옵션인자      | 설명  | 기본값 |
| ------------- | ----- | ---- |
| seunjeon.compress | 사전 압축모드. "true" 또는 "false"을 값으로 받습니다.| -Xmx1g 이하에서는 기본값이 "true" 가 됩니다.<br> <i>ES_JAVA_OPTS="-Dseunjeon.compress=true" ./bin/elasticsearch</i> |


## elasticsearch 플러그인 개발
```bash
# 사전 다운로드
./scripts/download-dict.sh mecab-ko-dic-2.0.1-20150920

# 사전 빌드(mecab-ko-dic/* -> src/main/resources/*.dat)
#   src/main/resources/ 디렉토리에 컴파일된 사전들이 만들어집니다.
sbt -J-Xmx2G "run-main org.bitbucket.eunjeon.seunjeon.DictBuilder"

# zip 생성
sbt
> project elasticsearch
> esZip
```

## License
Copyright 2015 유영호, 이용운. 아파치 라이센스 2.0에 따라 소프트웨어를 사용, 재배포 할 수 있습니다. 더 자세한 사항은 http://www.apache.org/licenses/LICENSE-2.0 을 참조하시기 바랍니다.