import java.util.Scanner;

public class Solution03 {
    public static void main(String[] args) {
//        run1();
        run2();
    }

    static void run1() {
        try {
            Scanner sc = new Scanner(System.in);
            int a = sc.nextInt();
            int b = sc.nextInt();
            System.out.println(a / b);
            sc.close(); // 에러 시 여기에 도달하지 못함
            System.out.println("자원 반납완료");
        } catch (Exception e) {
            System.out.println("Exception");
        }
    }

    static void run2() {
//        Scanner sc; // 선언만 한 상태는 객체가 부여된 것이 아니므로 close 반환 X
//        Scanner sc = null; // 강제로 null이라도 넣어서 활성화되었다고 가정
        // 스코프 문제 때문에 일반적으로 NullPointerException이 나옴.
        Scanner sc = new Scanner(System.in);
        try {
//            sc = new Scanner(System.in);
            int a = sc.nextInt();
            int b = sc.nextInt();
            System.out.println(a / b);
            sc.close(); // 중복된 코드를 방어적으로 짜는 등의 문제
        } catch (Exception e) {
            System.out.println("Exception");
        } finally {
            // 스코프 문제가 생김
            sc.close(); // sc가 null로 시작하기 때문에 NullPointerException 문제가 있음.
            System.out.println("자원 반납완료");
        }
    }

    static void run3() {
        // try () <- try-with-resources
        try (Scanner sc = new Scanner(System.in)) {
            // ...
        } // 알아서 finally에서 close가 호출된다는 것을 보장
        // AI에게 시켜놓으면
        // 1. 아예 자원반환 따위는 신경쓰지 않는 상-프로그래머
        // 2. 자원반환을 신경쓰는 방어적/보수적 코드를 하나, 코드의 중복 혹은 잘못된 호출들이 발생
        // 3. try-with-resources를 명시해놓으면 사용함. 내가 해당 구문을 모르면 '에러'인가 싶음.

        // 1. 지침 상 효율적인 아키텍처, 신문법, 여러가지 문법 슈가 등 중에 본인이 선호/필요한 것을 기술
        // 2. 애초에 토큰을 덜 먹게 쪼개서 파일을 몇줄 이내에 읽어들여서 많은 토큰을 쓰지 않게
        // (1)에서 효율적을 설계를 권유/강제/지침으로 줘야 (2)가 됨.
    }
}