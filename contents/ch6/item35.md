# ordinal 메서드 대신 인스턴스 필드를 사용하라

- enum에서 상수는 자기 자신이 몇 번째 위치인지를 나타내는 ordinal이라는 메서드를 제공한다. 다만 이 ordinal을 이용해서 코드를 작성한다면 유지보수하기 힘들어지는 이슈가 발생한다.

```java
public enum Ensemble {
    SOLO, DUET, TRIO, ...;
    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

- 위 ENUM에서 상수의 위치를 바꾼다거나, 다른 상수와 같은 정수를 사용하는 또 다른 상수를 추가할 방법이 없다. 또 값을 중간에 비워둘 수도 없다.

## Enum 타입 상수에 연결된 값은 ordinal 메서드로 얻지 말고 인스턴스 필드에 저장하자

```java
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);

    private final int numberOfMusicians;
    Ensemble(int size) { this.numberOfMusicians = size; }
    public int numberOfMusicians() { return numberOfMusicians; }
}
```

- ordinal 메소드를 직접  쓸 일은 없을 것이며, Enum 기반 자료구조에 쓸 목적으로 설계된 것이다. 쓰지 말자.
