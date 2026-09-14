# 자바 예외 처리(Exception), 파일 입출력 스트림 & 정규표현식 📂

<!-- workspace-readme-learning:start -->
## 파일과 연결한 학습 안내

아래 설명은 이 폴더의 실제 소스와 빌드 설정을 기준으로 정리했습니다. 기존 소개의 기능 설명은 연결된 파일과 함께 확인할 수 있습니다.

### 주요 파일과 역할

| 파일 | 역할과 읽을 내용 |
| --- | --- |
| [src/oop/AppService.java](<src/oop/AppService.java>) | 업무 처리와 외부 의존성 호출 — `run` |
| [01_java_exception_handling.md](<01_java_exception_handling.md>) | 설계·학습·운영 내용을 설명하는 문서 |
| [02_java_custom_exceptions.md](<02_java_custom_exceptions.md>) | 설계·학습·운영 내용을 설명하는 문서 |
| [03_java_resource_cleanup.md](<03_java_resource_cleanup.md>) | 설계·학습·운영 내용을 설명하는 문서 |
| [04_java_exception_propagation.md](<04_java_exception_propagation.md>) | 설계·학습·운영 내용을 설명하는 문서 |
| [05_java_byte_stream.md](<05_java_byte_stream.md>) | 설계·학습·운영 내용을 설명하는 문서 |
| [06_java_character_stream.md](<06_java_character_stream.md>) | 설계·학습·운영 내용을 설명하는 문서 |
| [07_java_buffered_stream.md](<07_java_buffered_stream.md>) | 설계·학습·운영 내용을 설명하는 문서 |
| [08_java_api_and_regex.md](<08_java_api_and_regex.md>) | 설계·학습·운영 내용을 설명하는 문서 |
| [src/oop/App.java](<src/oop/App.java>) | Java 타입과 동작 정의 — `run` |
| [src/oop/AppProvider.java](<src/oop/AppProvider.java>) | Java 타입과 동작 정의 — `run` |
| [src/Solution01.java](<src/Solution01.java>) | main에서 실행하는 Java 실습 — `main`, `method1`, `method2` |
| [src/Solution02.java](<src/Solution02.java>) | main에서 실행하는 Java 실습 — `main` |
| [src/Solution03.java](<src/Solution03.java>) | main에서 실행하는 Java 실습 — `main`, `run1`, `run2` |
| [src/Solution04.java](<src/Solution04.java>) | main에서 실행하는 Java 실습 — `main` |
| [src/Solution05.java](<src/Solution05.java>) | main에서 실행하는 Java 실습 — `main`, `saveByByteStream`, `loadByByteStream` |
| [src/Solution06.java](<src/Solution06.java>) | main에서 실행하는 Java 실습 — `main`, `saveByCharStream`, `loadByCharStream` |
| [src/Solution07.java](<src/Solution07.java>) | main에서 실행하는 Java 실습 — `main`, `useScannerWithBuffer`, `readTextWithBuffer` |

### 실행과 설정 확인

- [src/Solution01.java](<src/Solution01.java>)의 main부터 IDE에서 실행합니다. 패키지 선언과 사용하는 JDK API를 확인합니다.
- 코드·설정에서 참조하는 환경 변수 이름: `NAVER_CLIENT_ID`, `NAVER_CLIENT_SECRET`. 기본값과 필수 여부는 각 참조 위치에서 확인합니다.

### 관련 PDF와 보충 설명

- [6/26 강의](<../260629_ex/새 폴더/6-26/README.md>): 예외 전달·자원 수명·파일 및 HTTP 입출력을 연결합니다.

이 링크는 구현을 이해하기 위한 관련 기초 자료입니다. 해당 강의가 이 저장소의 모든 기능이나 이후 버전의 API를 설명한다는 뜻은 아닙니다.

### 읽는 순서와 복습

- 자원 생성 → 사용 → 실패 전달 → 정리 순으로 확인합니다. 파일 부재·문자 인코딩·외부 요청 실패를 구분하고 예외 원인이 보존되는지 확인합니다.

<!-- workspace-readme-learning:end -->

자바 프로그래밍의 실전 가동성을 보장하는 예외 처리 기법, 외부 파일에 데이터를 읽고 쓰는 파일 입출력 스트림(Byte/Character/Buffered), 그리고 텍스트 패턴을 분석하는 정규표현식(Regex)을 종합 학습하는 프로젝트입니다.

---

## 📂 학습 파일 구성 (Files)

- [src/Solution01.java](<src/Solution01.java>) ~ [src/Solution08.java](<src/Solution08.java>) : 예외 처리 및 입출력 알고리즘 코드
- [01_java_exception_handling.md](<01_java_exception_handling.md>) ~ [04_java_exception_propagation.md](<04_java_exception_propagation.md>) : 예외의 종류, 커스텀 예외 정의, 자원 회수(`try-with-resources`) 이론 정리
- [05_java_byte_stream.md](<05_java_byte_stream.md>) ~ [07_java_buffered_stream.md](<07_java_buffered_stream.md>) : 파일 바이트 입력, 캐릭터 버퍼 스트림 성능 분석
- [08_java_api_and_regex.md](<08_java_api_and_regex.md>) : 이메일, 전화번호 등의 형식이 맞는지 점검하는 정규표현식 기본

---

## 🛠 배운 핵심 개념 (What We Learned)

- **예외 복구 (try-catch-finally)**: 실행 도중 예상치 못한 오류가 터져도 비정상 종료를 예방하고 적절히 에러를 우회/로그 기록하는 흐름을 배웁니다.
- **스트림 (Stream)**: 파일이나 네트워크를 통해 들어오는 무수한 데이터를 한 바이트씩 혹은 줄 단위로 버퍼링하여 빠르고 누수 없이 읽고 쓰는 원리를 마스터합니다.

---

## 🚀 실행 및 확인 방법 (How to Run)

1. 개별 `Solution` 파일이나 스트림 예제 코드를 열어 컴파일하고, 지정된 텍스트 파일(e.g., `file1.txt`)에 데이터가 정상 기록되는지 파일 탐색기에서 확인합니다.

<!-- pdf-til-supplement:start -->
## TIL 부연 설명 — PDF와 연결하기

기존 실습 내용을 이해하기 위한 PDF 기반 부연 설명이다. 아래 예시는 개념을 설명하기 위한 것이며, 이 프로젝트에서 실행해 관찰한 결과와는 구분한다. 페이지 번호는 표지를 포함한 PDF 순서다.

함께 읽을 파일: [src/oop/AppService.java](<src/oop/AppService.java>) · [src/oop/App.java](<src/oop/App.java>) · [src/oop/AppProvider.java](<src/oop/AppProvider.java>)

### 예외를 처리할 위치와 원인 보존

예외를 잡았다고 문제가 해결되는 것은 아니다. 현재 계층에서 복구할 수 있으면 처리하고, 그렇지 않으면 호출자가 판단할 수 있는 형태로 전달한다. 체크 예외를 업무 예외로 바꿀 때 원인 예외를 함께 보관하면 최초 실패 지점을 추적할 수 있다.

**예시로 이해하기:** 파일 읽기 실패를 빈 문자열로 바꾸면 “파일이 비어 있음”과 “읽지 못함”을 구분할 수 없다. 실패 원인과 작업 맥락을 남기고 사용자에게는 필요한 안내를 전달한다. catch에서 성공처럼 계속 진행하는 코드가 없는지 확인한다.

근거: 222-1 Java 예외 처리 — [12쪽](<../260629_ex/새 폴더/6-26/222-1_Java_예외_처리.pdf#page=12>) · [14쪽](<../260629_ex/새 폴더/6-26/222-1_Java_예외_처리.pdf#page=14>) · [15쪽](<../260629_ex/새 폴더/6-26/222-1_Java_예외_처리.pdf#page=15>) · [16쪽](<../260629_ex/새 폴더/6-26/222-1_Java_예외_처리.pdf#page=16>)

### 문자 인코딩과 자원 수명

바이트 스트림은 원시 데이터를, Reader·Writer는 문자 데이터를 다룬다. 텍스트 파일의 바이트와 Java 문자열 사이에는 인코딩·디코딩 단계가 있으므로 양쪽 문자셋을 맞춰야 한다. 이미지 파일에 문자 스트림을 사용하면 원본 바이트가 변형될 수 있다.

**예시로 이해하기:** 텍스트를 UTF-8로 저장했다면 읽을 때도 UTF-8을 지정한다. try-with-resources로 닫을 자원을 묶으면 성공·실패 양쪽에서 정리할 수 있다. 큰 파일은 한 번에 메모리에 올리는 방식과 버퍼로 나누어 읽는 방식의 메모리 사용 차이를 생각해 본다.

근거: 222-2 Java File I O — [4쪽](<../260629_ex/새 폴더/6-26/222-2_Java_File_I_O.pdf#page=4>) · [5쪽](<../260629_ex/새 폴더/6-26/222-2_Java_File_I_O.pdf#page=5>) · [8쪽](<../260629_ex/새 폴더/6-26/222-2_Java_File_I_O.pdf#page=8>)

<!-- pdf-til-supplement:end -->
