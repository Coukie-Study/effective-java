# 커스텀 직렬화 형태를 고려하라.
### 고민하고 괜찮다고 판단 될 때만 기본 직렬화 형태를 사용하라.
개발 일정에 쫓겨 API 설계에 집중을 하면 다음 릴리즈에서 제데로 구현하기로 하고, 일단 동작만 하도록 만들어 놓을 수 있다.
<br> 직렬화 또한 마찬가지로 기본 직렬화(implements Serializable 만 명시) 만 으로 직렬화를 하고 다음 릴리즈에서 
<br> 수정한 직렬화로 변경하고 싶을 수 있다. 하지만 이렇게 할 경우 다음 릴리즈에서 기본 직렬화를 변경하지 못하게 된다.
<br> 실제 BigInteger 와 같은 자바의 일부 클래스들이 이러한 문제를 겪고있다.

### 기본 직렬화 형태에 적합한 경우
객체의 물리적 표현과 논리적 내용이 같다면 기본 직렬화 형태라도 무방하다.
````java
public class Name implements Serializable{
    /**
     * 성. null이 아니어야 함.
     * @serial
     */
    private final String lastName;
    
    /**
     * 이름. null이 아니어야 함.
     * @serial
     */
    private final String firstName;

    /**
     * 중간이름. 중가이름이 없다면 null.
     * @serial
     */
    private final String middleName;

    ... // 나머지 코드 생략.
}
````

기본 직렬화 형태가 적합하다고 결정했더라도 불변식 보장과 보안을 위해 readObject 메서드를 제공해야 할 때가 많다.
<br> 위의 경우 readObject 메서드가 lastName과 firstName 필드가 null이 아님을 보장해야 한다.

### 기본 직렬화 형태에 적합하지 않은 경우
````java
public final class StringList implements Serializable{
    private int size = 0;
    private Entry head = null;

    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }      
}
````

위 클래스는 논리적으로는 일련의 문자열을 표현하였지만, 물리적으로는 문자열을 이중 연결 리스트로 연결하였다.
<br> 이 클래스에 기본 직렬화를 사용하면 각 노두의 양방향 연결 정보를 포함해 모든 Entry를 철두철미하게 기록한다.

### 객체의 물리적 표현과 논리적 표현의 차이가 클 때 기본 직렬화 형태를 사용했을시 생기는 문제
1. 공개 API가 현재의 내부 표현 방식에 영구히 묶인다.
2. 너무 많은 공간을 차지할 수 있다.
3. 시간이 너무 많이 걸릴 수 있다.
4. 스택 오버플로를 일읠 수 있다.

### 적절한 커스텀 직렬화 형태를 갖춘 StringList
````java
// transient 한정자는 해당 인스턴스 필드가 기본 직렬화 형태에 포함되지 않는다는 표시다.

public final class StringList implements Serializable {
    private transient int size = 0;
    private transient Entry head = null;

    // 이번에는 직렬화 하지 않는다.
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    // 문자열을 리스트에 추가한다.
    public final void add(String s) { ... }

    /**
     * StringList 인스턴스를 직렬화한다.
     */
    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(size);

        // 모든 원소를 순서대로 기록한다.
        for (Entry e = head; e != null; e = e.next) {
            s.writeObject(e.data);
        }
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int numElements = stream.readInt();

        for (int i = 0; i < numElements; i++) {
            add((String) stream.readObject());
        }
    }
    // ... 생략
}
````
위처럼 커스텀 직렬화를 사용하면 용량, 시간, 스택오버플로우에 대해 성능을 모두 개선시킬 수 있다.
### 유의사항
1. 클래스의 모든 필드가 transient여도 defaultWriteObject, defaultReadObject를 호출 해야 한다.
2. 기본 직렬화를 사용하면 transient 필드들은 역직렬화될 때 기본값으로 초기화된다.
3. 동기화를 원한다면 writeObject도 syncrhronized로 선언해야 한다.
4. 어떤 직렬화 형태를 선택하든 직렬화 가능 클래스 모두에 직렬 버전 UID를 명시적으로 부여해야 한다.