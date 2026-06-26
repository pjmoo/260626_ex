# Java 사용자 정의 예외와 다중 Catch 블록 정리

본 문서는 [Solution02.java](file:///Users/morgan/Documents/workspace/260626_ex/src/Solution02.java) 실습 예제 코드를 기반으로 Java의 사용자
정의 예외(Custom Exception)와 다중 예외 처리(Multi-catch) 방식에 대해 상세히 설명합니다.

---

## 1. 사용자 정의 예외 (Custom Exception)

Java에서 내장된 예외 클래스 외에 비즈니스 요구사항에 따라 직접 예외 클래스를 정의하여 사용할 수 있습니다. 이때 상속하는 부모 클래스에 따라 Checked 또는 Unchecked 예외가 결정됩니다.

### ① Checked Custom Exception (`CustomException`)

`Exception` 클래스를 상속받아 선언하며, 컴파일 시점에 명시적인 예외 처리를 요구합니다.

```java
class CustomException extends Exception {
    public final int code; // 커스텀 에러 코드 추가
    private static final String defaultMessage = "너 무슨 짓을 저지른 거야?";

    CustomException() {
        super(defaultMessage);
        code = 100;
    }

    CustomException(String message) {
        super(message);
        code = 101;
    }

    CustomException(String message, int code) {
        super(message);
        this.code = code;
    }
}
```

* **상태 필드 추가**: 단순히 메시지뿐만 아니라 오류 상황을 식별할 수 있는 상태 값(예: `code`)을 내부에 정의하고, 다양한 생성자(Constructor Overloading)를 제공하여 유연성을
  높였습니다.

### ② Unchecked Custom Exception (`UncheckedCustomException`)

`RuntimeException` 클래스를 상속받아 선언하며, 컴파일 시점의 명시적 예외 처리가 필요하지 않은 런타임 예외입니다.

```java
class UncheckedCustomException extends RuntimeException {
}
```

---

## 2. 다중 Catch 블록 (Multi-catch) 및 예외 순서

동일한 `try` 블록 내에서 여러 종류의 예외가 던져질 때, 예외 종류에 따라 다르게 대응할 수 있습니다.

### ① 멀티 Catch (`|` 연산자)

자바 7부터 지원되는 구문으로, 중복 코드를 줄이기 위해 서로 다른 예외 클래스를 기호 `|`로 연결하여 하나의 `catch` 블록에서 처리할 수 있습니다.

```java
try{
        // 예외가 던져질 수 있는 구간
        }catch(CustomException |
UncheckedCustomException e){
        System.out.

println("e.getMessage() = "+e.getMessage());
        System.out.

println("e.getClass() = "+e.getClass());
        }
```

### ② Catch 블록의 순서 규칙 (Hierarchy Constraint)

여러 개의 `catch` 블록을 순서대로 늘어놓을 때, **자식 예외(구체적인 예외)**가 **부모 예외(상위 예외)**보다 먼저 작성되어야 합니다.

```java
try{
        // 예외 발생
        }catch(CustomException e){
        // 1순위: 가장 구체적인 CustomException 처리
        }catch(
RuntimeException ex){
        // 2순위: Unchecked 예외 처리
        }catch(
Exception e){
        // 3순위: 그 외 모든 일반 Exception 처리 (상위 클래스이므로 가장 마지막에 위치해야 함)
        }
```

* **순서가 잘못되는 경우 (컴파일 에러)**:
  만약 `catch (Exception e)`가 `catch (CustomException e)`보다 위에 오게 되면, 하위 타입인 `CustomException`은 항상 상위 catch 블록에 의해 걸러져서
  처리되므로 컴파일 오류(`Exception '...' has already been caught`)가 발생합니다.
* **업캐스팅 관계가 아닌 경우**:
  두 예외 클래스가 서로 상속 관계가 아닌 경우(예: `CustomException`과 `UncheckedCustomException`)는 순서에 상관이 없으며, 멀티 Catch(`|`)로 동시에 묶어서 처리할 수
  있습니다.