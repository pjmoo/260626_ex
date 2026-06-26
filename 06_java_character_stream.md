# Java 문자 스트림 (Character Stream) 입출력 정리

본 문서는 [Solution06.java](file:///Users/morgan/Documents/workspace/260626_ex/src/Solution06.java) 실습 예제 코드를 기반으로 Java의 **
문자 스트림(Character Stream)**의 특징과 구현법, 그리고 바이트 스트림과의 차이점을 정리합니다.

---

## 1. 문자 스트림 (Character Stream)의 특징

바이트 스트림이 1바이트(8비트) 단위로 기계적인 데이터를 읽고 쓴다면, 문자 스트림은 **문자(2바이트/16비트 유니코드)** 단위를 기준으로 데이터를 읽고 씁니다.

* **다국어(한글 등) 지원**: 영어 이외의 문자(한글, 한문 등)는 UTF-8 또는 UTF-16 기준 2~3바이트로 표현되기 때문에 바이트 스트림으로 읽을 경우 깨지기 쉽습니다. 문자 스트림은 자바 내부의
  유니코드 변환과 인코딩 처리를 자동으로 지원하므로 깨지지 않고 입출력됩니다.
* **최상위 추상 클래스**: `Reader` (입력), `Writer` (출력)
* **대표 구현 클래스**: `FileReader`, `FileWriter`

---

## 2. 파일 쓰기 (`FileWriter`)

`FileWriter`를 이용하면 인코딩된 바이트 배열로 직접 변환하지 않고 문자열 그대로 바로 저장할 수 있습니다.

```java
private static void saveByCharStream(String text, String charFileName) {
    try (FileWriter writer = new FileWriter(charFileName)) {
        writer.write(text); // 1) getBytes() 없이 문자열 직접 전달 가능
        System.out.println("문자 스트림 작성 완료");
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

---

## 3. 파일 읽기 (`FileReader`)

`FileReader.read()`는 파일에서 문자 하나(2바이트 또는 인코딩 문자 단위)를 읽어 정수형(int)으로 반환합니다.

```java
private static void loadByCharStream(String charFileName) {
    try (FileReader reader = new FileReader(charFileName)) {
        int c;
        // read() 메서드는 유니코드 문자 단위로 하나씩 읽으며, 더 이상 읽을 문자가 없으면 -1을 반환합니다.
        while ((c = reader.read()) != -1) {
            System.out.print((char) c); // 2바이트(유니코드 문자) 단위로 한 문자씩 캐스팅하여 출력
        }
        System.out.println();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

### 💡 바이트 스트림 대비 개선점

이전 [05_java_byte_stream.md](file:///Users/morgan/Documents/workspace/260626_ex/05_java_byte_stream.md)에서
`FileInputStream`을 사용했을 때와 달리, `FileReader`는 유니코드 단위로 문자열을 파싱하므로 `"안녕하세요 Java입니다 잘 부탁드립니다"`와 같은 한글 텍스트도 **글자가 깨지지 않고
정상적으로 화면에 출력**됩니다.