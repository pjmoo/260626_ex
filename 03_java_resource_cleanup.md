# Java 예외 처리와 자원 반납 (finally) 정리

본 문서는 [Solution03.java](file:///Users/morgan/Documents/workspace/260626_ex/src/Solution03.java) 실습 예제 코드를 기반으로 Java의 예외
처리 중 자원 반납(Resource Cleanup)의 중요성과 `finally` 블록의 역할에 대해 상세히 설명합니다.

---

## 1. 자원 누수 (Resource Leak)와 반납의 필요성

컴퓨터의 메모리, 파일 핸들러, 네트워크 소켓, 데이터베이스 커넥션, 입출력 스트림 등은 한정된 시스템 자원(Resource)입니다. 사용이 끝난 자원은 명시적으로 반납(`close()`)해 주어야 합니다.

반납하지 않을 경우 자원이 계속 점유 상태로 남아 있는 **자원 누수(Resource Leak)**가 발생하여 시스템의 성능 저하 또는 마비로 이어질 수 있습니다.

---

## 2. 예외 발생 시 자원 반납 누락 문제

### 예시: `run1()` 메서드 분석

```java
static void run1() {
    try {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println(a / b); // 만약 b에 0이 입력되면 ArithmeticException 발생
        sc.close(); // [이동 불가] 예외 발생 시 이 코드는 실행되지 않음
        System.out.println("자원 반납완료");
    } catch (Exception e) {
        System.out.println("Exception");
    }
}
```

* **문제점**: `a / b` 구문에서 예외가 발생하면 바로 `catch` 블록으로 실행 흐름이 건너뛰기 때문에, 뒤에 있는 `sc.close()`가 실행되지 못한 채 자원이 누수됩니다.

---

## 3. `finally` 블록을 이용한 자원 반납 보장

`finally` 블록은 `try` 블록 안에서 예외가 발생하든 발생하지 않든, 혹은 `catch` 블록에서 또 다른 예외나 `return`이 실행되더라도 **무조건 실행되는 블록**입니다.

### 예시: `run2()` 메서드 분석 (finally 개선안 및 고려사항)

```java
static void run2() {
    Scanner sc = new Scanner(System.in); // 선언과 할당을 블록 외부에서 완료
    try {
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println(a / b);
        sc.close(); // 방어적으로 try 블록 내부에서도 중복 호출 가능 (다만 중복 코드 및 관리 오버헤드 발생)
    } catch (Exception e) {
        System.out.println("Exception");
    } finally {
        sc.close(); // 예외 발생 여부와 상관없이 확실하게 반납 보장
        System.out.println("자원 반납완료");
    }
}
```

### 💡 주요 체크 포인트 (Scope와 초기화)

1. **변수 스코프(Scope)**:
    * `try` 블록 내부에서 `Scanner sc = new Scanner(System.in);`과 같이 선언하면 `finally` 블록에서 변수 `sc`에 접근할 수 없습니다. 따라서 `Scanner sc`
      는 `try`문 이전에 선언되어야 합니다.
2. **초기화 시점과 NullPointerException**:
    * `Scanner sc = null;`로 선언하고 `try` 블록 내부에서 객체를 생성할 경우, 객체 생성 전에 예외가 발생하면 `finally` 블록의 `sc.close()`에서
      `NullPointerException`이 유발될 수 있는 잠재적 위험이 존재합니다.
    * 따라서 자원을 미리 안전하게 생성해두고 시작하거나, `finally` 내부에서 null 체크(`if (sc != null)`) 처리를 동반하는 것이 전통적인 해결법이었습니다.

---

## 4. try-with-resources 구문을 통한 현대적 자원 반납

Java 7부터 제공되는 **try-with-resources** 구문을 사용하면 `finally`에서 명시적으로 `close()`를 호출하지 않아도 자동으로 자원이 반납됩니다.

### 예시: `run3()` 메서드 분석

```java
static void run3() {
    // try () 괄호 안에 자동 반납할 자원 객체 생성
    try (Scanner sc = new Scanner(System.in)) {
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println(a / b);
    } catch (Exception e) {
        System.out.println("Exception");
    } // 블록을 벗어나는 순간 자동으로 close()가 호출됨이 보장됨
}
```

### 💡 특징 및 이점

* **자동 반납 조건**: `try()` 내부에 선언할 수 있는 객체는 반드시 **`AutoCloseable`** 인터페이스를 구현하고 있어야 합니다. (`Scanner` 등 대부분의 standard I/O,
  File I/O, JDBC 객체들이 이를 구현하고 있습니다.)
* **가독성 향상**: 중복 코드가 제거되고 자원 관리를 컴파일러와 런타임에 전적으로 위임하므로 코드가 간결해집니다.
* **실수 방지**: 프로그래머가 명시적으로 `close()` 호출을 누락하여 발생하는 자원 누수 버그를 원천적으로 방지합니다.