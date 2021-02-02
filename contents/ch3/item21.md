# 아이템 21. 인터페이스는 구현하는 쪽을 생각해 설계하라.

### 디폴트 메서드란?
디폴트 메서드(default method)란 java 8에 추가된 기능으로 인터페이스에서 직접 구현한 메서드이다.<br>

### 인터페이스 메서드 추가
- ####자바 8 전

    인터페이스에 선언된 메서드는 구현체에서 무조건 구현해야 하는데 구현체에 이미 추가할 메서드가 구현되어 있을 확률은 거의 없기때문에 인터페이스에 메서드를 추가 할 방법이 없었다. 

- ####자바 8 이후

    디폴트 메서드를 이용하면 인터페이스에 추가 할 메서드를 구현체에서 구현할 필요가 없기 때문에<br>
    인터페이스에 메서드를 추가할 수 있게 되었다.
    
### 디폴트 메서드의 위험성
#### 1. 재정의 하지 않은 디폴트 메서드는 불변식을 해칠 수 있다.
````java
//Collection 인터페이스 removeIf 메서
default boolean removeIf(Predicate<? super E> filter) {
    Objects.requireNonNull(filter);
    boolean removed = false;
    final Iterator<E> each = iterator();
    while (each.hasNext()) {
        if (filter.test(each.next())) {
            each.remove();
            removed = true;
        }
    }
    return removed;
}
````
[apache SynchronizedCollection](https://commons.apache.org/proper/commons-collections/jacoco/org.apache.commons.collections4.collection/SynchronizedCollection.java.html) 은 클라이언트가 제공한 객체에 락을 걸어 모든 메서드에 락 객체로 동기화한 후 내부 컬렉션 객체에 기능을 위임하는 래퍼 클래스다.<br>
하지만 이 클래스는 removeIf를 재정의 하고 있지 않아 removeIf 메서드는 동기화 해주지 못한다.
<br><br>
이를 예방하기 위해 구현체에서는 디폴트 메서드를 재정의하여 디폴트 메서드 호출 전 필요한 작업을 수행해야한다. 
````java
//Collections.SynchronizedCollection
@Override
public boolean removeIf(Predicate<? super E> filter) {
    synchronized (mutex) {return c.removeIf(filter);}
}
````
<br>

#### 2. 디폴트 메서드는 기존 구현체에 런타임 오류를 일으킬 수 있다.
흔한 일은 아니지만 일어나지 않으리란 보장이 없다. 자바 8은 컬렉션 인터페이스에 꽤 많은 디폴트 메서드를 추가했고<br>
그 결과 기존에 짜여진 많은 자바코드가 많은 영향을 받았다. 

### 디폴트 메서드 추가시 주의사항
- 새 메서드를 추가하는 일은 꼭 필요한 경우가 아니면 피해야 한다.
- 기존 구현체들과 충돌할 가능성을 심사숙고 해야한다.
- 인터페이스의 메서드를 제거하거나 기존 메서드의 시그니처를 수정하는 용도로 사용하면 안된다.
- 디폴트 메서드 추가 후 서로 다른 방식의 구현체들로 실험을 해봐야 한다.
