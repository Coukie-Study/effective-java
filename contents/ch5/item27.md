# 비검사 경고를 제거하라

```java
public static void main(String[] args) {
    List<String> lists = new ArrayList();
}
```

- 제네릭을 사용하면 수 많은 컴파일러 경고를 마주치게 됨(Intellij는 경고 강조)
- 위의 예제에서도 다이아몬드 연산자를 쓰지 않아 타입 매개변수 관련 경고가 뜨는 것을 알 수 있다.
    - 다이아몬드 연산자는 자바 7부터 지원하며, 컴파일러가 실제 타입을 추론해 줌

- 가능한 모든 비검사 경고를 제거해보자
    - 타입 안정성이 보장된다.
    - 런타임에 ClassCastException이 발생할 수 없다.
    - 코드가 의도대로 동작한다.
- 경고를 제거할 수 없지만 타입 안전하다고 확신할 수 있다면 `@SuppressWarnings("unchecked")`를 달아 경고를 숨기는 것을 추천
    - 숨기지 않는다면 새로운 컴파일 경고가 파묻힐 수 있다.
- `@SuppressWarnings("unchecked")` 애너테이션은 항상 좁은 범위에 적용
    - 이 경우에도 다른 경고가 파묻힐 수 있는 위험이 있기 때문.

```java
// ArrayList.java

public <T> T[] toArray(T[] a) {
    if (a.length < size)
        // Make a new array of a's runtime type, but my contents:
        return (T[]) Arrays.copyOf(elementData, size, a.getClass());
    System.arraycopy(elementData, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```

- 위의 경우 `return` 시 `@SuppressWarnings("unchecked")`을 직접 달 수는 없지만 지역 변수를 추출해 경고를 달면 가장 좁게 경고를 달 수 있다.

```java
public <T> T[] toArray(T[] a) {
    if (a.length < size) {
        @SuppressWarnings("unchecked") T[] result = 
            (T[]) Arrays.copyOf(elementData, size, a.getClass())
        return result;
    }
    System.arraycopy(elementData, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```