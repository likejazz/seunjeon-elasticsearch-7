* elasticsearch 에서 index 별로 다른 사용자 사전 사용할 수 있게 하자.
  * 설정에 추가한 사용자 사전은 인스턴스 별로
  * 파일에서 읽은 사용자 사전은 파일 변경 추적
* lexicon feature부분 미리 list로 만들지말고 string 으로 보관할까? (사용자 사전이 커지면 메모리 문제가 있을 것 같음)
* mecab-ko-dic/char.def 읽어들이지 않고 하드코딩할까? (코드 복잡도가 높음...)
* 기분석 단어 사용자 사전 등록
* 사용자사전
  * feature 정의 가능하게
  * surface + 품사 입력
  * surface + 품사 + cost 입력
