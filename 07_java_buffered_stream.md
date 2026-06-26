# Java 성능 최적화를 위한 버퍼 스트림 (NIO & Buffered Stream)

본 문서는 [Solution07.java](file:///Users/morgan/Documents/workspace/260626_ex/src/Solution07.java) 실습 예제 코드를 기반으로 Java의 **
버퍼 스트림(Buffered Stream)**의 개념, Java NIO 입출력과의 연계, 그리고 대량의 데이터를 효율적으로 저장 및 처리하는 기법을 정리합니다.

---

## 1. 버퍼 스트림 (Buffered Stream)이란?

컴퓨터에서 디스크(HDD/SSD)나 네트워크 장비와의 입출력(I/O)은 CPU 속도에 비해 매우 느린 **무거운 작업(System Call)**입니다.

* **기존 스트림**: 글자 하나 혹은 바이트 하나가 입력/출력될 때마다 즉시 디스크 장치에 접근하므로 병목현상이 발생합니다.
* **버퍼 스트림**: 입출력 장치 사이에 **메모리 버퍼(Buffer)** 공간을 둡니다. 데이터를 쓸 때 임시 버퍼 공간에 차곡차곡 모았다가, 버퍼가 가득 차거나 강제로 내보낼(`flush`/`close`) 때
  디스크에 한 번에 출력하여 입출력 횟수를 대폭 낮추고 효율성을 극대화합니다.

---

## 2. Java NIO 기반의 버퍼 출력 (`BufferedWriter`)

Java 7부터 도입된 NIO 패키지의 `Files`, `Paths` 클래스를 연계하여 간결하게 버퍼 출력 스트림을 열 수 있습니다.

```java
private static void writeTextWithBuffer(String file) {
    // Files.newBufferedWriter를 사용하여 Paths 경로에 해당하는 버퍼 출력 스트림 생성
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file))) {
        writer.write("반갑습니다\n");
        writer.write("반갑습니다");
        writer.newLine(); // 1) OS 독립적인 줄바꿈(개행) 지원
        writer.write("JDK17+로 작업중");
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

* **`newLine()`**: OS 환경마다 줄바꿈 문자(Windows: `\r\n`, Mac/Linux: `\n`)가 다른데, `newLine()` 메서드는 실행 중인 운영체제 환경에 부합하는 개행문자를
  자동으로 삽입해 주어 이식성을 보장합니다.

---

## 3. Java NIO 기반의 버퍼 입력 (`BufferedReader`)

`BufferedReader`는 데이터를 한 줄씩 통째로 읽어 들일 수 있는 강력한 편의 메서드인 `readLine()`을 제공합니다.

```java
private static void readTextWithBuffer(String file) {
    Path path = Paths.get(file);
    try (BufferedReader reader = Files.newBufferedReader(path)) {
        String line;
        // readLine()은 파일 끝에 도달하면 null을 반환합니다.
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // 한 줄씩 읽어 콘솔에 출력
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

* **`readLine()`**: 데이터를 문자 단위가 아닌 줄바꿈 단위로 파싱하여 문자열 스트링 객체로 반환하므로 텍스트 파싱 및 파일 데이터 판독 생산성을 획기적으로 향상시킵니다.

---

## 4. 응용: Scanner 입력과 BufferedWriter 연동

콘솔에서 상호작용으로 데이터를 무한히 입력받고, 특정 신호(`w`)가 오기 전까지 메모리에 담아두었다가 파일에 한 번에 쓰는 로직입니다.

```java
private static void useScannerWithBuffer(String file) {
    List<String> lines = new ArrayList<>();
    // 1) 콘솔 입력용 Scanner 획득
    try (Scanner sc = new Scanner(System.in)) {
        System.out.println("[저장할 텍스트를 입력하세요] 저장은 w");
        while (true) {
            String input = sc.nextLine();
            if (input.equals("w")) {
                break; // 'w' 입력 시 종료
            }
            lines.add(input); // 메모리(List)에 축적
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }

    // 2) 메모리에 누적된 데이터를 BufferedWriter를 통해 한 번에 파일로 출력
    Path path = Paths.get(file);
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
        for (String line : lines) {
            writer.write(line);
            writer.newLine(); // 한 줄 입력 후 줄바꿈 보장
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```