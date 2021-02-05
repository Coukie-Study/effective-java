# 로 타입은 사용하지 말라

## 제네릭

- 제네릭 클래스 : 클래스와 인터페이스 선언에 타입 매개변수가 쓰인 경우, 2개를 동시에 제네릭 타입이라 한다.

```java
public class Box<T> {
    private final T element;

    public Box(T element) {
        this.element = element;
    }

    public T getElement() {
        return element;
    }
}
```

- 매개 변수화 타입 : `List<String>`
- 실제 타입 매개변수 : `String`
- 제네릭 타입 : `List<E>`
- 정규 타입 매개변수 : `E`

## Raw type

- 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않은 것 ex) `List`
- 제네릭이 만들어지기 전과 호환성을 위해 두었다.

### 단점

```java
private final Collection stamps = ...;

// 아무런 문제 없이 동작(경고 메시지만 출력)
stamp.add(new Coin())

for (Iterator i = stamps.iterator(); i.hasNext(); ) {
    Stamp stamp = (Stamp) i.next(); // ClassCastException
    ...
}
```

- 런타임 시에만 오류가 발생해 문제를 알아차리기 힘들다.
- 원인과 동떨어지게 되어 실제 오류를 찾기 힘들어진다.
- 제네릭을 사용한다면 컴파일 시에 확인 가능하다.

```java
private final Collection<Stamp> stamps = ...;

stamp.add(new Coin()) // 컴파일 오류
```

- 일반적으로 제네릭을 사용하면 얻을 수 있는 안전성과 표현력을 모두 잃게 된다.
- 따라서 기존 코드와의 호환성을 위해 남긴 것을 제외하고는 절대로 로 타입을 쓰면 안된다.

## `List` vs `List<Object>`

- 결론적으로 **`List<Object>`**
- `List<Object>`는 컴파일러에 명확히 모든 타입을 허용한다는 의미를 전달한다.
- 제네릭 하위타입 규칙
    - 제네릭을 사용한 `List<String>`는 `List`의 하위 타입이지만 `List<Object>`의 하위 타입은 아니다.

    ```java
    public class Box<T> {
        private final T element;

        public Box(T element) {
            this.element = element;
        }
        
        static void printRaw(Box box) {
            System.out.println(box);
        }

        static void print(Box<Object> box) {
            System.out.println(box);
        }

        public static void main(String[] args) {
            print(new Box("hello"));
            print(new Box<String>("hello")); // 컴파일 에러
        }
    }
    ```

    - 로 타입을 사용하면 결과적으로 타입 안전성을 잃게 된다.

    ```java
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        unsafeAdd(strings, Integer.valueuOf(42));
        String s = strings.get(0); // 런타임 에러
    }

    private static void unsafeAdd(List list, Object o) {
        list.add(o);
    }

    // 다음과 같이 바꾸면 컴파일 에러
    private static void unsafeAdd(List<Object> list, Object o) {
        list.add(o);
    }
    ```

## 비한정적 와일드카드 타입

```java
static int numElementsInCommon(Set s1, Set s2) {
    int result = 0;
    for (Object o1 : s1)
        if (s2.contains(o1))
            result++;
    return result;
}
```

- 위에서 원소의 타입을 신경쓰지 않고 오로지 공통 원소를 반환받고 싶을 때 위와 같이 로 타입을 고려해볼 수 있겠지만, 로 타입은 안전하지 않다.
- 비한정적 와일드카드 타입(Unbounded Wildcard Type)을 사용한다면 실제 타입 매개변수가 무엇인지 신경 쓰지 않아도 되며 타입 안전하다.

### 비한정적 와일드카드 타입(Unbounded Wildcard Type) 예시

```java
static int numElementsInCommon(Set<?> s1, Set<?> s2) {
    int result = 0;
    for (Object o1 : s1)
        if (s2.contains(o1))
            result++;
    return result;
}
```

```java
static void hello(Set s1, Set<?> s2) {
    s1.add("hello");
    s2.add("hello"); // 컴파일 에러
}
```

- 위에서 매개변수 `s2`는 `null` 이외의 어떤 변수도 넣지 못하며, 컬렉션 안의 요소 타입도 알지 못하게 된다.
- 제너릭 메서드나 한정적 와일드카드 타입(Bounded Wildcard Type)을 사용하면 위의 제약을 해결할 수 있다.

## 예외

- `class` 리터럴에는 로 타입을 써야 한다. `List<String>.class`같은 경우는 쓰지 못한다.
- `instanceof` 연산자 사용시에는 런타임 시 제네릭 타입 정보가 지워지므로 매개변화 타입을 적용할 수 없다.(비한정적 와일드카드 타입 제외) 그리고 `instanceof` 에서는 비한정자 와일드카드 타입을 쓰더라도 아무런 역할을 하지 못하므로 다음과 같이 쓰자.

```java
if (o instanceof Set) {
    Set<?> s = (Set<?>) o;
}
```