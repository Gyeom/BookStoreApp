# BookStoreApp
1. [도서검색앱] 구현
- [Search], [Detail Book] 2개의 화면을 가진다.
- [Search] 화면에서 결과값 중 하나를 선택하면 세부정보를 보여주는 [Detail Book] 화면을
보여준다
2. 세부 구현 내용

    1) [Search]: 특정 키워드에 대한 서적 검색 정보를 보여준다.
        - 특정 키워드를 입력받을 수 있도록 한다. 키워드는 최대 2개이고 ‘or’ 와 ‘not’ operator가 로 구분된다.
        - JSON으로 넘어오는 모든 정보(이미지 포함)를 보여주어야 한다.
        - 첫번째 API 요청에 대한 응답 결과는 첫번째 페이지만 보여준다.
        - 검색 결과를 스크롤링을 사용하여 페이지 구분을 없앤다. 즉, 검색 화면은 스크롤을 사용하여 부드럽게 모든 결과를 볼 수 있어야 한다.
        - ‘or(|)’ operator는 두 키워드중 하나의 키워드가 제목에 포함된 서적들을 보여준다. (e.g. ‘java|kotlin’ : java 또는 kotlin이 제목에 포함된 서적들을 검색해 보여준다.)
        - ‘not(-)’ operator는 앞의 키워드가 제목에 포함된 서적을을 검색하되 뒤의 키워드가 포함되지 않은 서적들을 보여준다. (e.g. ‘android-java’ : android가 제목에 포함된 서적을 검색하되 java라는 키워드를 가지고 있는 서적은 제외한다.)
        - API(GET): https://api.itbook.store/1.0/search
        - API(GET): https://api.itbook.store/1.0/search/{query}/{page}
    2) [Detail Book]: 서적 리스트 중 선택된 서적의 상세 정보를 보여준다.
        - JSON으로 넘어오는 모든 정보(이미지 포함)를 보여주어야 한다.
        - API(GET): https://api.itbook.store/1.0/books/{isbn13}
 * Kotlin으로 작성해주세요.
