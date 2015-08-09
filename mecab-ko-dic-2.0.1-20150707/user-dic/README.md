# 사용자 사전 추가

## 준비
[mecab-ko](https://bitbucket.org/eunjeon/mecab-ko)와 [mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic) 을 다운받아 설치합니다. mecab-ko-dic 을 꼭 컴파일까지 하셔야 이후에 사전 추가가 가능합니다.

## 사전 추가
내려받은 mecab-ko-dic/userdic 디렉토리 안에 csv 확장자로 사전 파일을 추가합니다.

    :::text
    userdic/
    ├── nnp.csv
    ├── person.csv
    └── place.csv


  * 일반적인 고유명사 추가
    
        :::text
        대우,,,,NNP,*,F,대우,*,*,*,*
        구글,,,,NNP,*,T,구글,*,*,*,*

  * 인명 추가

        :::text
        까비,,,,NNP,인명,F,까비,*,*,*,*
    
  * 지명 추가

        :::text
        세종,,,,NNP,지명,T,세종,*,*,*,*
        세종시,,,,NNP,지명,F,세종시,Compound,*,*,세종/NNP/지명+시/NNG/*

그 외의 품사 추가가 필요한 경우에는 [품사태그표](https://docs.google.com/spreadsheet/ccc?key=0ApcJghR6UMXxdEdURGY2YzIwb3dSZ290RFpSaUkzZ0E&usp=sharing#gid=4) 를 참고하세요.

 
## 사전 컴파일
    :::text
    $ mecab-ko-dic/tools/add-userdic.sh

아래와 같이 user-xxx.csv 사전이 추가된 모습을 볼 수 있습니다. 사실 아래 파일은 컴파일 되기 직전의 파일이며, 실제로 sys.dic 파일에 바이너리로 컴파일 되어 들어가게 됩니다.

    :::text
    mecab-ko-dic
    ├── ....
    ├── user-nnp.csv
    ├── user-person.csv
    ├── user-place.csv
    └── ...

## 설치
    :::text
    $ make install

