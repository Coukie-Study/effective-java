# 이왕이면 제네릭 타입으로 만들라

## Stack 제네릭 적용

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    // The elements array will contain only E instances from push(E).
    // This is sufficient to ensure type safety, but the runtime
    // type of the array won't be E[]; it will always be Object[]!
    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // Eliminate obsolete reference
        return result;
    }
    ...
}
```

- 위에서 만든 stack은 제네릭이 없는 상태이다. 런타임 오류가 날 위험이 존재(형변환 과정에서)

## 제네릭을 적용하는 방법

- 일반 클래스를 제네릭 클래스로 만드는 첫 단계는 클래스 선언에 타입 매개 변수를 추가하는 것. 스택의 경우는 스택이 담을 타입 하나만 추가(보통 `E`를 사용)

### 첫번째 방법

- `Object`를 쓴 곳을 타입 매개변수로 바꾸자

```java
public class Stack<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    // The elements array will contain only E instances from push(E).
    // This is sufficient to ensure type safety, but the runtime
    // type of the array won't be E[]; it will always be Object[]!
    public Stack() {
        elements = new E[DEFAULT_INITIAL_CAPACITY]; // error
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0)
            throw new EmptyStackException();
        E result = elements[--size];
        elements[size] = null; // Eliminate obsolete reference
        return result;
    }
    ...
}
```

- 위와 같이 수정할 경우 `E`는 실체화 불가 타입으로 배열을 만들 수 없기 때문에 `Object` 배열을 생성한 다음 제네릭 배열로 형변환 하는 방법이다.

```java
elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
```

- 위와 같이 구성할 경우 컴파일러는 경고를 내보내지만
    - elements가 private로 저장이 되고 클라이언트로 반환되거나 다른 메서드에 전달되는 일이 없으며, push 메서드를 통해 저장되는 원소의 타입은 항상 `E`이다.
- 비검사 형변환이 안전한 것을 직접 증명했으므로 `@SuppressWarnings("unchecked")`로 해당 경고를 숨기자

### 두 번째 방법

```java
public class Stack<E> {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    // Appropriate suppression of unchecked warning
    public E pop() {
        if (size == 0)
            throw new EmptyStackException();

        // push requires elements to be of type E, so cast is correct
        @SuppressWarnings("unchecked") E result =
                (E) elements[--size];

        elements[size] = null; // Eliminate obsolete reference
        return result;
    }
```

- 위 첫번째 방법과는 다르게 필드의 타입을 `Object[]`로 두고 반환 시 형변환을 한다.
- 형 변환시에 마찬가지로 안전한지 컴파일러가 증명할 수 없으므로 `@SuppressWarnings("unchecked")`를 이용해 경고를 숨긴다.
- 1은 2에 비해 코드도 짧고 가독성도 더 좋다.
- 다만 배열의 런타임 타입과 컴파일타임 타입이 달라 힙 오염을 일으킨다.

```java
public class HeapPollutionDemo
{
    public static void main(String[] args)
    {
        Set s = new TreeSet<Integer>();
        Set<String> ss = s;              // unchecked warning
        s.add(new Integer(42));          // another unchecked warning
        Iterator<String> iter = ss.iterator();

        while (iter.hasNext())
        {
            String str = iter.next();    // ClassCastException thrown
            System.out.println(str);
        }
    }
}
```

- 스택의 예에서는 힙 오염으로 인한 해가 없었다.

## 배열을 사용하는 경우

- 사실 제네릭 타입 안에서 리스트를 사용하는 게 항상 가능하지도, 꼭 더 좋은 것도 아니다.
- 자바는 리스트 타입을 기본 타입으로 제공하지 않으므로 ArrayList 같은 제네릭 타입도 결국 기본 타입인 배열로 구성해야 한다.
- 성능을 높일 목적으로 배열을 사용하기도 한다.

## 타입 매개변수의 제약

- 대다수의 제네릭 타입은 타입 매개변수에 아무런 제약을 두지 않는다.
- 다만 기본 타입은 사용할 수 없어 `Integer`, `Long`과 같은 박싱타입을 사용해 우회해야 한다.
- 타입에 제약을 두는 제네릭 타입(한정적 타입 매개변수)
    - 형 변환 없이 상위 타입의 메서드 호출 가능

```java
public class Box<E extends Number> {
    private E number;

    public void test() {
        number.intvalue();
    }
}
```