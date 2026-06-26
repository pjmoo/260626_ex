# Java Open API 연동 및 정규표현식(Regex) 기반 데이터 처리

본 문서는 [Solution08.java](file:///Users/morgan/Documents/workspace/260626_ex/src/Solution08.java) 실습 예제 코드를 기반으로 Java를 이용한
HTTP Open API 호출, 환경변수 관리, 정규표현식을 이용한 데이터 파싱, 그리고 결과 파일 저장 기법을 정리합니다.

---

## 1. 안전한 API 인증 정보 관리

보안이 중요한 API Key나 Client Secret 등은 소스코드에 하드코딩하지 않고 환경변수 또는 `.env` 파일을 활용해 관리합니다.

* **환경변수 로드**: `System.getenv("VARIABLE_NAME")`을 호출하여 시스템에 등록된 변수값을 가져옵니다.
* **보안 위험 방지**: `.gitignore` 파일에 `.env` 등을 등록하여 원격 저장소(GitHub 등)에 민감 정보가 업로드되는 것을 원천적으로 차단합니다.
* **사용자 가이드 제공**: 협업 시 다른 개발자가 필요한 환경변수 정보를 파악할 수
  있도록 [.env.sample](file:///Users/morgan/Documents/workspace/260626_ex/.env.sample) 형태로 환경 구성 템플릿만 공유합니다.

---

## 2. HTTP 클라이언트를 이용한 네이버 이미지 검색 API 호출

Java 11부터 표준 스펙으로 편입된 `HttpClient`를 사용하여 비동기/동기 HTTP 요청을 간단하게 전송할 수 있습니다.

```java
// 1) HTTP 클라이언트 및 요청 객체 생성
HttpClient client = HttpClient.newHttpClient();

// 2) 한글 검색어 인코딩 처리 (URL Safe String 변환)
String encodedQuery = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
String url = "https://openapi.naver.com/v1/search/image?query=%s&display=%d&start=%d&sort=sim"
        .formatted(encodedQuery, 5, 1);

// 3) 헤더 구성 및 요청 빌드
HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .headers("X-Naver-Client-Id", clientId, "X-Naver-Client-Secret", clientSecret)
        .build();

// 4) 요청 전송 및 응답(String 형태) 획득
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
String body = response.body();
```

---

## 3. 정규표현식(Regex)을 이용한 비정형 JSON 파싱

경량 구조의 실습에서 무거운 외부 JSON 파서 라이브러리(Jackson, Gson 등)를 추가하지 않고, Java 내장 정규표현식(`Pattern`, `Matcher`)만으로 필요한 특정 필드 데이터를 고속으로
스크랩할 수 있습니다.

```java
// JSON 내부의 "link":"http://..." 형태의 데이터 매칭
Pattern pattern = Pattern.compile("\"link\":\"(.*?)\"");

for(
MatchResult matchResult :pattern.

matcher(body).

results().

toList()){
// 괄호 ( )로 묶인 그룹1의 값(실제 URL) 추출 및 이스케이프 슬래시(\/) 정상화
String imageUrl = matchResult.group(1).replace("\\/", "/");
    urlList.

add(imageUrl);
}
```

* **슬래시 정규화**: JSON 문자열 직렬화 과정에서 슬래시(`/`) 문자 앞에 붙는 이스케이프 기호(`\/`)를 `.replace("\\/", "/")`를 이용하여 완전한 URL 주소 체계로 복원합니다.

---

## 4. 파일 저장 및 동적 파일명 생성

NIO의 `BufferedWriter`를 이용하여 대량의 URL 목록을 줄바꿈 단위로 저장합니다. 실행 시마다 파일명이 겹치지 않도록 키워드와 현재 서울 표준시 기준 타임스탬프를 조합해 고유 파일명을 설정합니다.

```java
private static void saveUrlList(List<String> urlList, String keyword) {
    // 공백을 언더바로 변환하고 날짜시간 스탬프 삽입
    String fileName = "%s-%s.txt".formatted(
            keyword.replace(" ", "_"),
            ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"))
    );

    Path path = Paths.get(fileName);
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
        for (String url : urlList) {
            writer.write(url);
            writer.newLine(); // OS 독립적 개행 삽입
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

* **`ZonedDateTime`**: 타임존(`Asia/Seoul`)을 명시하여 로컬 서버나 클라우드(GitHub Actions 등) 환경에 관계없이 항상 동일한 한국 표준시 기준으로 타임스탬프가 찍히도록
  보장합니다.

---

## 5. 이미지 바이너리 파일 다운로드 (`HttpResponse.BodyHandlers.ofFile`)

수집된 이미지 URL을 기반으로 실제 이미지 파일 데이터를 원격지에서 다운로드해 로컬 저장소에 저장하는 로직입니다.

```java
private static void downloadImages(List<String> urlList, String keyword) {
    HttpClient httpClient = HttpClient.newHttpClient();
    for (String url : urlList) {
        System.out.println("url = " + url);
        // 1) 파일 확장자 추출 및 유효성 검사
        String[] tmp = url.split("\\.");
        String ext = tmp[tmp.length - 1];
        if (ext.length() > 4) { // 확장자가 비정상적으로 긴 경우 예외처리 및 건너뛰기
            System.out.println("확장자 오류");
            continue;
        }

        // 2) 고유 저장 파일명 생성
        Path path = Paths.get("%s-%d.%s".formatted(keyword, System.currentTimeMillis(), ext));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        try {
            // 3) Response BodyHandler로 ofFile(path)을 설정하여 스트림 복사 없이 파일로 즉시 기록
            HttpResponse<Path> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofFile(path));
            System.out.println("response = " + response.uri());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### 💡 주요 핵심 기술

* **`HttpResponse.BodyHandlers.ofFile(path)`**: 원격지 리소스를 로컬 파일 시스템으로 바로 다운로드받을 수 있도록 기본 제공되는 핸들러입니다. 개발자가 별도로 입력 스트림(
  `InputStream`)을 받아 파일 출력 스트림(`FileOutputStream`)에 쓰고 버퍼를 비우는 귀찮은 복사 로직을 수작업으로 짤 필요가 없게 해줍니다.
* **확장자 분리 (`split`) 및 방어 코드**: URL 구조상 이미지 파일 경로 끝의 확장자(jpg, png 등)를 파싱할 때 쿼리 스트링 등으로 인해 확장자 길이가 비정상적일 경우(
  `ext.length() > 4`) 다운로드를 우회하여 비정상 파일 생성을 예방합니다.