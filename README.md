# seunjeon for Elasticsearch 7 or newer
analysis-seunjeon(은전한닢) is the best Korean analyzer, especially useful for Elasticsearch. Unfortunately [original seunjeon repo](https://bitbucket.org/eunjeon/seunjeon/) hasn't been upgraded since 2018 and doesn't work with the latest version of Elasticsearch. So I've patched the module to work properly in Elasticsearch 7 and It's very easy to use as shown below:

## How to Install
If you install analysis-seunjeon on Elasticsearch 7.16.3:
```bash
$ bin/elasticsearch-plugin install https://github.com/likejazz/seunjeon-elasticsearch-7/releases/download/7.16.3/analysis-seunjeon-7.16.3.zip 
```

## Release History

| Supported Elasticsearch version | release note | release date |
| --------------------------------| ------------ | ------------ |
| 8.0.0, 8.1.2                    | Releases for Elasticsearch 8 | Feb 22, 2022 |
| 7.16.3, 7.16.2, 7.9.1           | Patched to work properly on Elasticsearch 7 | Jan 13, 2022 |

## How to Use
```
PUT seunjeon-test
{
  "settings": {
    "index": {
      "analysis": {
        "tokenizer": {
          "seunjeon_tokenizer": {
            "type": "seunjeon_tokenizer",
            "index_eojeol": false,
            "decompound": true,
            "pos_tagging": false,
            "index_poses": [
              "UNK",
              "EP",
              "I",
              "M",
              "N",
              "SL",
              "SH",
              "SN",
              "V",
              "VCP",
              "XP",
              "XS",
              "XR"
            ]
          }
        },
        "analyzer": {
          "korean": {
            "type": "custom",
            "tokenizer": "seunjeon_tokenizer"
          }
        }
      }
    }
  }
}

GET seunjeon-test/_analyze
{
  "text": "홍대입구에서 강남역까지",
  "analyzer": "korean"
}
--
{
  "tokens" : [
    {
      "token" : "홍대",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "N",
      "position" : 0
    },
    {
      "token" : "입구",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "N",
      "position" : 1
    },
    {
      "token" : "강남",
      "start_offset" : 7,
      "end_offset" : 9,
      "type" : "N",
      "position" : 2
    },
    {
      "token" : "역",
      "start_offset" : 9,
      "end_offset" : 10,
      "type" : "N",
      "position" : 3
    }
  ]
}
```

## Settings
| setting      | desc  | default |
| ------------- | ----- | ---- |
| `user_words`    | 사용자 사전        | `[]`     |
| `user_dict_path`| 사용자 사전 파일, base directory는 ES_HOME/config 입니다. 사전파일 예제는 [user_dict.csv](https://bitbucket.org/eunjeon/seunjeon/raw/master/elasticsearch/scripts/user_dict.csv)를 참고하세요. |  |
| `decompound`    | 복합명사 분해      | `true` |
| `deinflect`     | 활용어의 원형 추출 | `true` |
| `index_eojeol`  | 어절 추출     | `true` |
| `index_poses`   | 추출할 품사        | `["N", "SL", "SH", "SN", "XR", "V", "M", "UNK"]` |
| `pos_tagging`   | 품사태깅, 키워드에 품사를 붙여서 토큰을 뽑습니다.        | `true` |
| `max_unk_length`  | unknown 키워드로 뽑을 수 있는 최대 길이(한글) | 8 |

* 사용사 사전은 하나만 관리하기 떄문에 여러개의 tokenizer를 생성해도 마지막 로드된 사전만 유지됩니다.
* `user_words`와 `user_dict_path`를 함께 설정할 경우 `user_words`는 무시되고 `user_dict_path`만 적용됩니다.
* `"pos_tagging": true` 의 경우 키워드와 품사가 함께 토큰(e.g. `자전거/N`)으로 나오기 때문에 stopword filter나 synonym filter 사용시 적용이 안될 수 있습니다. `"pos_tagging": false`로 설정을 하여 사용하거나, filter 사전을 `자전거/N`의 형태로 만들어야 합니다.

## 품사태그표
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

## JVM options
| setting      | desc  | default |
| ------------- | ----- | ---- |
| `seunjeon.compress` | 사전 압축모드, `true` 또는 `false`를 값으로 받습니다.| `-Xmx1g` 이하에서는 기본값이 `true`가 됩니다. `_ES_JAVA_OPTS="-Dseunjeon.compress=true" ./bin/elasticsearch` |

## How to Build
Requirements: JDK 1.8
```bash
# Download Dictionary
$ ./scripts/download-dict.sh mecab-ko-dic-2.1.1-20180720

# Build Dictionary(mecab-ko-dic/* -> src/main/resources/*.dat)
$ sbt -J-Xmx2G "runMain org.bitbucket.eunjeon.seunjeon.DictBuilder"
```

### How to Release
```
$ cd elasticsearch/target
$ zip analysis-seunjeon-8.0.0.zip analysis-seunjeon-8.0.0.jar plugin-descriptor.properties
```

# Make zip archive
```
$ sbt
> project elasticsearch
> esZip
```

## References
- [엘라스틱서치 7을 위한 은전한닢 형태소 분석기](https://docs.likejazz.com/seunjeon-elasticsearch-7/)
- [Official seunjeon Bitbucket](https://bitbucket.org/eunjeon/seunjeon/)
- [은전한닢 프로젝트](http://eunjeon.blogspot.com/)
- [seunjeon for OpenSearch](https://bitbucket.org/soosinha/seunjeon-opensearch/)

## License
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
