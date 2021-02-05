# 배열보다는 리스트를 사용하라

## 배열 vs 제네릭

- 배열은 공변이지만 반면, 제네릭은 불공변이다.
    - `Sub` 클래스와 `Super`가 있을 때, `Sub`가 `Super`의 하위타입이면 `Sub[]`또한 `Super[]`의 하위 타입이다.
    - 서로 다른 타입 `Type1`, `Type2`가 있을 때, `List<Type1>`과 `List<Type2>`는 상위, 하위 타입 관계가 아니다.

    ```java
    Object[] objectArray = new Long[1];
    objectArray[0] = "타입 맞니?" // ArrayStoreException을 던진다.

    List<Object> lists = new ArrayList<Long>(); // 컴파일 에러.
    ```

- 배열은 실체화가 된다.
    - 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다.
    - 위 코드에서 Long 배열에 String을 넣으려 하면 ArrayStoreException이 발생한다.
    - 반면 제네릭은 타입 정보가 런타임에는 소거된다.
    - 이는 제네릭이 지원되기 전의 레거시 코드와 제네릭 타입을 함께 사용할 수 있게 해주는 메커니즘이다.

## 제네릭 배열이 지원되지 않는 이유

```java
List<String>[] stringLists = new List<String>[1]; // 1
List<Integer> intList = List.of(42); // 2
Object[] objects = stringLists; // 3
objects[0] = intList; // 4
String s = stringLists[0].get(0); //  5
```

- 만약 1이 허용된다고 가정할 경우
- 3은 `List`가 `Object`의 하위 타입이니 문제가 없고
- 4는 제네릭은 런타임에 소거 되기 때문에 `List`가 원소에 들어가는 상황이라 문제가 없다.
- 하지만 5에서 직접 원소를 꺼낼 경우 `String`으로 자동 형변환이 되는데 이때 `ClassCastException`이 발생한다.
- 따라서 1에서 컴파일 오류를 내야 한다.

### [실체화 불가 타입](https://docs.oracle.com/javase/tutorial/java/generics/nonReifiableVarargsType.html#non-reifiable-types)

- E, List<E>, List<String>
- 실체화되지 않아서 런타임에는 컴파일 타임보다 타입 정보를 적게 가지는 타입
- [소거 메커니즘](https://www.baeldung.com/java-type-erasure) 때문에 매개변수화 타입 가운데 실체화될 수 있는 타입은 비한정적 와일드카드 타입뿐이다.
- 배열을 비한정적 와일드 카드 타입으로 만들 수는 있지만, 유용하게 쓰이지는 못한다.

### 배열을 제네릭으로 만들지 못해 나타나는 불편한 경우

- 제네릭 컬렉션에서는 자신의 원소 타입을 담은 배열을 반환하는 게 보통은 불가능하다.
- 또한 제네릭 타입과 가변인수 메서드를 함께 사용하면 해석하기 어려운 경고 메세지를 받는다. (실체화 불가 타입일 경우) - item 32 참고

## 배열 대신 컬렉션을 사용하자

- 배열로 형변환할 때 제네릭 배열 생성 오류나 비검사 형변환 경고가 뜨는 경우 배열말고 컬렉션을 사용하자

```java
public class Chooser<T> {
    private final T[] choiceList;

    public Chooser(Collection<T> choices) {
        choiceList = choices.toArray();
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
```

- toArray();가 Object[]를 반환하기에 타입캐스팅을 해주어야 한다. `(T[])`
- 다만 이 경우 컴파일러는 경고를 발생시킨다. (런타임에 안전함을 보장 X)
- 가장 간단한 방법은 `Collection`을 사용하는 것

```java
public class Chooser<T> {
    private final List<T> choiceList;

    public Chooser(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
```