# 박싱된 기본 타입보다는 기본 타입을 사용하라

- 기본타입 : int, long, char, boolean...
- 참조타입 : Integer, Long, Character, Boolean...

- 오토박싱, 언박싱으로 우리는 위의 타입을 혼용해서 사용할 수 있지만, 위 두가지 타입은 차이점이 존재한다.
    1. 기본 타입은 값만 가지고 있으나, 박싱된 기본 타입은 값에 더해 식별성이란 속성을 갖는다.

    ```java
    Long l1 = Long.valueOf(127);
    Long l2 = Long.valueOf(127);
    System.out.println(l1 == l2);
    ```

    2. null값을 가질 수 있다.

    3. 기본 타입이 박싱된 기본 타입보다 시간과 메모리 사용면에서 더 효율적이다.

- 위의 주의사항을 고려하지 않은 예제

```java
public static void main(String[] args) {

    Comparator<Integer> naturalOrder =
              (i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);

    int result = naturalOrder.compare(new Integer(42), new Integer(42));
    System.out.println(result);
}
```

- 위 예제에서 `i == j`부분에서 다른 비교자와 달리 오토 언박싱되지 않고 박싱된 객체들을 비교한다. 즉 객체의 식별성을 비교하기 때문에 잘못된 결과가 도출될 수 있다.
- 고친 예제

```java
// Fixed Comparator - Page 274
Comparator<Integer> naturalOrder = (iBoxed, jBoxed) -> {
    int i = iBoxed, j = jBoxed; // Auto-unboxing
    return i < j ? -1 : (i == j ? 0 : 1);
};
```

- 혹은 `Comparator.naturalOrder()`를 사용

- 또 다른 예제

```java
static Integer i;

public static void main(String[] args) {
    if (i == 42)
        System.out.println("Unbelievable");
}
```

- 위의 예제에서 `i == 42`를 검사할 때, 어떤 문자열도 출력되지는 않지만 NullPointerException이 나온다. 그 이유는 참조 타입의 기본값은 null이고, 참조 타입이 42(기본 타입)과 비교되는 과정에서 오토 언박싱이 일어나는데 null은 오토 언박싱이 불가능하기 때문이다.

- 마지막 예제

```java
public static void main(String[] args) {
    Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++) {
        sum += i;
    }
    System.out.println(sum);
}
```

- sum은 참조 타입으로 두어 값을 계산할 때마다 박싱과 언박싱이 같이 일어난다.

- 참조타입은 언제 써야 하나?
    - 컬렉션의 원소, 키, 값으로 쓴다.
    - 리플렉션을 통해 메서드를 호출할 때