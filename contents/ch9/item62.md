# 다른 타입이 적절하다면 문자열 사용을 피하라

- 문자열을 쓰지 말아야 하는 경우
    1. 문자열은 다른 값 타입을 대신하기에 적합하지 않다.
        - 사용자에게 입력받은 문자열을 적절한 타입으로 변환해 사용해야 한다.
    2. 문자열은 열거 타입을 대신하기에 적합하지 않다.
        - 상수를 열거할 때는 문자열보다 열거 타입이 월등히 낫다.
    3. 문자열은 혼합 타입을 대신하게에 적합하지 않다.
        - 여러 요소가 혼합된 데이터를 하나의 문자열로 표현하는 것은 대체로 좋지 않은 생각이다.

        ```java
        String compoundKey = className + "#" + i.next();
        ```

        - 위와 같이 혼합 타입을 표현할 경우 구분자가 요소 안에서 쓰였으면 혼란을 가지고 올 수 있고, 각 요소를 개별적으로 접근하기 위해 파싱도 해야 하는 불편함이 있다.
        - 적절한 equals, toString, compareTo 메서드도 제공할 수 없으며, String이 제공하는 기능에만 의존해야 한다.
        - 정적 멤버 클래스를 사용하자
    4. 문자열은 권한을 표현하기에도 적합하지 않다.
        - ThreadLocal(일종의 스레드 지역변수 기능)를 설계한다고 했을 때, 스레드를 구분하기 위한 방법으로 문자열 키를 사용했다.

        ```java
        public class ThreadLocal {
            private ThreadLocal() { }

            public static void set(String key, Object value);

            public static Object get(String key); 
        }
        ```

        - 위와 같은 방식의 문제점은 키가 클라이언트에 의해 중복될 수 있다는 점이다.
        - 또한 키를 악의적으로 사용해 다른 쓰레드가 사용하는 값을 가지고 올 수 있다.
        - 정적 내부 클래스를 활용해보자

        ```java
        public class ThreadLocal {
            private ThreadLocal() { }

            public static class Key {
                key() { }
            }

            public static Key getKey() {
                return new Key();
            }

            public static void set(Key key, Object value);

            public static Object get(Key key); 
        }
        ```

        - 문자열이 아니라 정적 내부 클래스를 사용하면 중복가능성과 악의적인 사용을 원천차단한다.
        - 위의 메소드에서 set과 get 메소드를 Key의 인스턴스 메소드로 바꿔도 상관이 없게 되며, 수정하면 Top class가 불필요하게 되어 다음과 같이 수정된다.

        ```java
        public final class ThreadLocal {
            public ThreadLocal() { }

            public void set(Object value);

            public Object get(); 
        }
        ```

        - 여기에 제네릭까지 추가하면 보다 타입 안전하게 만들 수 있다.