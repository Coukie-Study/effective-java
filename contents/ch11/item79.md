# 과도한 동기화는 피하라

- 과도한 동기화는 성능을 떨어뜨리고, 교착상태에 빠뜨리고, 예측할 수 없는 동작을 낳기도 한다.

### 응답 불가와 안전 실패를 피하려면 동기화 메서드나 동기화 블록 안에서는 제어를 절대로 클라이언트에 양도하면 안 된다.

- 동기화된 영역 안에서는 재정의할 수 있는 메서드는 호출하면 안 되며, 클라이언트가 넘겨준 함수 객체를 호출해서도 안 된다.
- 이런 메서드들을 이 책에서는 외계인 메서드로 표현하는데 무슨 일을 하는 지 알지 못하며, 통제할 수도 업다.
- 이는 교착 상태에 빠뜨리거나, 데이터를 훼손시킬 수 있다.

```java
public class ObservableSet<E> extends ForwardingSet<E> {
    public ObservableSet(Set<E> set) { super(set); }

    private final List<SetObserver<E>> observers
            = new ArrayList<>();

    public void addObserver(SetObserver<E> observer) {
        synchronized(observers) {
            observers.add(observer);
        }
    }

    public boolean removeObserver(SetObserver<E> observer) {
        synchronized(observers) {
            return observers.remove(observer);
        }
    }

    private void notifyElementAdded(E element) {
        synchronized(observers) {
            for (SetObserver<E> observer : observers)
                observer.added(this, element);
        }
    }

    @Override public boolean add(E element) {
        boolean added = super.add(element);
        if (added)
            notifyElementAdded(element);
        return added;
    }

    @Override public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E element : c)
            result |= add(element);  // Calls notifyElementAdded
        return result;
    }
}
```

```java
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;
    public ForwardingSet(Set<E> s) { this.s = s; }

    public void clear()               { s.clear();            }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty()          { return s.isEmpty();   }
    public int size()                 { return s.size();      }
    public Iterator<E> iterator()     { return s.iterator();  }
    public boolean add(E e)           { return s.add(e);      }
    public boolean remove(Object o)   { return s.remove(o);   }
    public boolean containsAll(Collection<?> c)
    { return s.containsAll(c); }
    public boolean addAll(Collection<? extends E> c)
    { return s.addAll(c);      }
    public boolean removeAll(Collection<?> c)
    { return s.removeAll(c);   }
    public boolean retainAll(Collection<?> c)
    { return s.retainAll(c);   }
    public Object[] toArray()          { return s.toArray();  }
    public <T> T[] toArray(T[] a)      { return s.toArray(a); }
    @Override public boolean equals(Object o)
    { return s.equals(o);  }
    @Override public int hashCode()    { return s.hashCode(); }
    @Override public String toString() { return s.toString(); }
}
```

```java
public interface SetObserver<E> {
    // Invoked when an element is added to the observable set
    void added(ObservableSet<E> set, E element);
}
```

- 일종의 옵저버 패턴을 구현한 set

```java
public class Test1 {
    public static void main(String[] args) {
        ObservableSet<Integer> set =
                new ObservableSet<>(new HashSet<>());

        set.addObserver((s, e) -> System.out.println(e));

        for (int i = 0; i < 100; i++)
            set.add(i);
    }
}
```

1. 23이면 자기 자신을 제거

    ```java
    public class Test2 {
        public static void main(String[] args) {
            ObservableSet<Integer> set =
                    new ObservableSet<>(new HashSet<>());

            set.addObserver(new SetObserver<>() {
                public void added(ObservableSet<Integer> s, Integer e) {
                    System.out.println(e);
                    if (e == 23)
                        s.removeObserver(this);
                }
            });

            for (int i = 0; i < 100; i++)
                set.add(i);
        }
    }
    ```

    - ConcurrentModificationException이 발생한다.
        - list를 순회하는 도중 수정하려고 하기 때문이다.
        - 자신이 콜백을 거쳐 되돌아와 수정하는 것까지 막지 못한다.
2. 백그라운드 스레드를 통해 제거 시도

    ```java
    public class Test3 {
        public static void main(String[] args) {
            ObservableSet<Integer> set =
                    new ObservableSet<>(new HashSet<>());

    // Observer that uses a background thread needlessly
            set.addObserver(new SetObserver<>() {
                public void added(ObservableSet<Integer> s, Integer e) {
                    System.out.println(e);
                    if (e == 23) {
                        ExecutorService exec =
                                Executors.newSingleThreadExecutor();
                        try {
                            exec.submit(() -> s.removeObserver(this)).get();
                        } catch (ExecutionException | InterruptedException ex) {
                            throw new AssertionError(ex);
                        } finally {
                            exec.shutdown();
                        }
                    }
                }
            });

            for (int i = 0; i < 100; i++)
                set.add(i);
        }
    }
    ```

    - 교착상태에 빠짐
3. 불변식이 임시로 깨진 경우
    - 자바 언어의 락은 재진입을 허용하므로 교착상태에 빠지지 않는다.
    - 다만 교착상태를 안전실패로 변모시킬 수 있다.

    ```java
    public class Reentrant{

        public synchronized outer(){
            inner();
        }

        public synchronized inner(){
            //do something
        }
    }
    ```

    - 이 문제를 해결하기 위해서는 외부 메서드 호출을 동기화 블록 바깥으로 옮기면 된다.

    ```java
    private void notifyElementAdded(E element) {
        synchronized(observers) {
            snapshot = new ArrayList<>(observers)
        }
        for (SetObserver<E> observer : snapshot)
            observer.added(this, element);
    }
    ```

    - CopyOnWirteArrayList를 이용해 위 로직을 간단히 할 수 있다.
    - 열린 호출 : 동기화 영역 바깥에서 호출되는 외계인 메서드
        - 실패 방지 효과와 동시성 효율을 개선 시켜준다.
- 동기화 영역에서는 가능한 일을 적게 하는 것이 추천된다.
- 성능 측면에서도 멀티코어가 일반화된 지금, 병렬로 실행할 기회를 잃고 모든 코어가 메모리를 일관되게 보기 위한 지연 시간이 있기에 과도한 동기화는 성능 저하를 이끈다.
    - 또한 코드 최적화도 제한하는 단점이 있다.
- 가변 클래스를 동기화 할 때 선택할 두가지 방안
    - 동기화를 전혀 사용하지 말고, 이 클래스를 사용할 클래스가 외부에서 동기화하게 하는 것
    - 내부에서만 동기화를 수행해 스레드 안전한 클래스로 만드는 것
        - 단 클라이언트가 외부에서 객체 전체에 락을 거는 것보다 동시성을 월등히 개선시킬 수 있을 때만 사용
            - StringBuffer, Random
        - 클래스 내부에서 동기화하기로 했다면 락 분할, 락 스트라이핑, 비차단 동시성 제어로 동시성을 높일 수 있다.
