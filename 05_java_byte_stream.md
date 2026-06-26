# Java 바이트 스트림 (Byte Stream) 입출력 정리

본 문서는 [Solution05.java](file:///Users/morgan/Documents/workspace/260626_ex/src/Solution05.java) 실습 예제 코드를 기반으로 Java의 입출력
스트림 중 **바이트 스트림(Byte Stream)**의 개념과 파일 읽기/쓰기 구현법, 그리고 발생할 수 있는 인코딩 주의사항을 정리합니다.

---

## 1. 입출력 스트림의 분류

Java I/O 스트림은 처리하는 데이터의 단위에 따라 두 가지로 분류됩니다.

* **바이트 스트림 (Byte Stream)**:
    * 1바이트(byte) 단위를 기준으로 데이터를 입출력합니다.
    * 동영상, 이미지, 음악 파일 등 이진 데이터(Binary Data)를 다루기에 적합합니다.
    * 대표 클래스: `InputStream`, `OutputStream` (하위로 `FileInputStream`, `FileOutputStream` 등)
* **문자 스트림 (Character Stream)**:
    * 2바이트(16비트 Unicode) 단위를 기준으로 텍스트를 입출력합니다.
    * 일반적인 텍스트 파일(.txt, .csv 등)을 인코딩 설정에 맞춰 읽고 쓰기에 적합합니다.
    * 대표 클래스: `Reader`, `Writer` (하위로 `FileReader`, `FileWriter` 등)

---

## 2. 파일 출력 스트림 (`FileOutputStream`)

문자열 데이터를 바이트 스트림으로 변환하여 파일에 저장하는 기능입니다.

```java
static void saveByByteStream(String text, String fileName) {
    try (FileOutputStream fos = new FileOutputStream(fileName)) {
        fos.write(text.getBytes(StandardCharsets.UTF_8)); // 1) 문자열을 UTF-8 바이트 배열로 변환 후 기록
        System.out.println("바이트 스트림으로 파일에 저장 완료");
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

* **동작 흐름**: `String.getBytes(StandardCharsets.UTF_8)` 메서드를 사용해 지정된 인코딩 형식의 byte 배열로 쪼갠 뒤, `FileOutputStream` 스트림에
  기록합니다.

---

## 3. 파일 입력 스트림 (`FileInputStream`)과 주의점

파일에서 데이터를 바이트 단위로 읽어와 출력하는 기능입니다.

```java
static void loadByByteStream(String fileName) {
    try (FileInputStream fis = new FileInputStream(fileName)) {
        int b;
        // read() 메서드는 파일 끝(EOF)에 도달하면 -1을 반환합니다.
        while ((b = fis.read()) != -1) {
            System.out.print((char) b); // 1바이트씩 문자(char)로 변환하여 출력
        }
        System.out.println();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

### ⚠️ 중요: 한글 등 다국어 깨짐 현상 (바이트 스트림의 한계)

* **현상**: 영문(ASCII) 데이터인 `"William"`은 1바이트당 1문자에 대응되므로 정상 출력됩니다. 하지만 **한글(UTF-8 기준 자당 3바이트)** 데이터인 `"반갑습니다"` 등은 1바이트씩
  쪼개진 상태에서 강제로 2바이트 문자타입인 `(char)`로 캐스팅되므로 글자가 깨지게 됩니다.
* **해결법**: 텍스트 데이터를 읽고 쓸 때는 바이트 스트림 대신 **문자 스트림(FileReader, BufferedReader 등)**을 사용해야 인코딩 단위에 맞춰 온전한 문자열로 복원할 수 있습니다.