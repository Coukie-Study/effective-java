# 다 쓴 객체 참조를 해제하라
Java는 C, C++과는 다르게 가비지 컬렉터(GC)를 통해 다 쓴 객체를 회수한다. 다만 몇 가지 부분에서 주의할 점이 존재한다.

## 1. 자기 메모리를 직접 관리할 경우
```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

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
        return elements[--size];
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
```
위의 경우 Stack에서 pop을 할 경우 단순히 현재 size를 줄이고 해당하는 값을 반환하는 형식으로 stack을 구현했다.
따라서 이미 pop한 객체는 elements에 남아있으며 다 쓴 객체더라도 elements에서 그 객체를 참조하기 때문에 GC에서 없애주지 않는다.
```java
public Object pop() {
    if (size == 0)
        throw new EmptyStackException();
    Object result = elements[--size];
    element[size] == null;
    return result;
}
```
다음과 같이 해당 배열 위치에서 null 처리(참조 해제)를 해야 정상적으로 GC가 동작한다.

그렇다고 모든 경우에 참조 해제를 직접 해줘야하는 것은 아니다. 객체 참조를 null로 하는 일은 예외적인 경우여야 하며, 
가장 좋은 방법은 유효범위 밖으로 참조를 담은 변수를 밀어내는 것이다.(더 이상 변수를 사용하는 경우가 없는 경우 GC)

## 캐시 사용 시
객체 참조를 캐시에 넣고 나서, 객체를 더 이상 사용하지 않아도 그 객체를 캐시에 넣어두면 GC가 되지 않아 메모리를 낭비시킨다.

```java
import java.util.HashMap;

public class Something {
    private static final Map<Integer, Something> CACHE = new HashMap<>();
    
    static {
        CACHE.put(1, new Something(1));
        CACHE.put(2, new Something(2));
    }
    // new Something(2)를 더이상 사용해지 않아도 캐시를 정리하지 않으면 계속 메모리에 남아있음
}
```
WeakHashMap을 사용해 캐시를 만들면 키가 외부에서 참조되는 경우에만 캐시에 남아있게 된다.

>An entry in a WeakHashMap will automatically be removed when its key is no longer in ordinary use. More precisely, the presence of a mapping for a given key will not prevent the key from being discarded by the garbage collector, that is, made finalizable, finalized, and then reclaimed. When a key has been discarded its entry is effectively removed from the map, so this class behaves somewhat differently from other Map implementations.

또는 쓰지 않는 엔트리를 삭제하기 위해 백그라운드 스레드를 활용하거나, 캐시를 추가할 때 부수 작업으로 수행하는 방법으로 처리 한다.
(ex. LinkedHashMap.removeEldestEntry을 Override) 
```java
protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
    // 이 메소드를 Override해서 조건에 맞으면 새로 put할 시, 가장 오래된 Entry를 삭제
    return false;
}
```
더 복잡하게 캐시를 구현해 효율적으로 관리할 수도 있다.(java.lang.ref 활용)
[ref에 대한 정보](https://d2.naver.com/helloworld/329631)
[예제](https://tourspace.tistory.com/42)
## 리스너, 콜백 사용 시
클라이언트가 콜백을 등록만 하고 명확하게 해지하지 않는다면, 콜백은 계속 쌓이게 된다. 따라서 콜백을 약한 참조로 저장하면 GC가 동작하게 된다.

```java
import java.util.HashMap;

public class Example {
    public static void main(String[] args) {
        Map<Hello, String> map = new HashMap<>();
        Hello hello = new Hello();
        map.put(hello, "Hello");
        // 맵에 쌓이게 된다.
        hello = null;
    }

    static class Hello {
    }
}
```