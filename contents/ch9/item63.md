# 문자열 연결은 느리니 주의하라

- 문자열은 불변이기 때문에 문자열 연결 연산자를 사용하면 n^2에 비례한다.
- 두 문자열을 연결한다면 양쪽의 내용을 모두 복사해야 하기 때문이다.
    - String은 기본적으로 참조타입

    ```java
    String hello = "Hello";
    for (int i = 0; i < 100; i++) {
        hello += i;
    }
    ```

    - 위와 같이 더하기 연산을 하면 기존 `"Hello"` 문자는 GC 대상이 되며 더하기 연산으로 새롭게 나온 `"Hello0"`, `"Hello1"` 등등도 모두 GC 대상이 된다. 이는 성능 하락의 원인이다.
- 가변 객체인 StringBuilder를 사용하자.

```java
public String statement2() {
    StringBuilder b = new StringBuilder(numItems() * LINE_WIDTH);
    for (int i = 0; i < numItems(); i++) {
        b.append(lineForItem(i));
    }
    return b.toString();
}
```

- StringBuilder는 선형시간으로 증가하지만, String은 제곱에 비례한다.

- StringBuilder vs StringBuffer
    - 둘의 차이점은 동기화 유무
    - StringBuffer는 동기화 키워드 지원
    - 단일 쓰레드일 경우는 StringBuffer를 사용하자