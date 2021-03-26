# wait와 notify보다는 동시성 유틸리티를 애용하라

- wait와 notify를 올바르게 사용하기 매우 까다로우니 고수준 동시성 유틸리티를 사용하자

## 동시성 컬렉션

- 표준 컬렉션 인터페이스에 동시성을 더해 구현한 고성능 컬렉션
- 높은 동시성에 도달하기 위해 동기화를 내부에서 수행
- 동시성을 무력화하는 건 불가능하며, 외부에서 락을 추가로 사용하면 속도가 오히려 느려진다.
- 상태 의존적 수정 메서드를 통해 기본 동작을 하나의 원자적 동작으로 묶어서 호출한다.
    - putIfAbsent
- ex) String.intern 구현

```java
public class Intern {
    // Concurrent canonicalizing map atop ConcurrentMap - not optimal
    private static final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<>();
		1)
    public static String intern(String s) {
        String previousValue = map.putIfAbsent(s, s);
        return previousValue == null ? s : previousValue;
    }

    2) 조금 더 개선한 버전
    // Concurrent canonicalizing map atop ConcurrentMap - faster!
    public static String intern(String s) {
        String result = map.get(s);
        if (result == null) {
            result = map.putIfAbsent(s, s);
            if (result == null)
                result = s;
        }
        return result;
    }
}
```

- 기존에 구현된 intern보다 속도가 빠르다.(단 기존 intern은 메모리 누수 방지 기술이 있어 느린 편이다.)
- 동시성 컬렉션이 빠르니 가급적 동시성 컬렉션을 사용하자.
    - Collections.synchronizedMap보다 ConcurrentHashMap이 빠르다.
- 컬렉션 인터페이스 중 BlockingQueue는 작업이 성공적으로 완료될 때까지 기다리도록 확장되었다.

## CountDownLatch, Semaphore

- 카운트다운 래치는 하나 이상의 스레드가 또 다른 하나 이상의 스레드 작업이 끝날 때까지 기다리게 한다. (int 값으로 들어온 값만큼 카운트다운 래치가 호출되면 그 때 스레드를 깨움)

```java
public class ConcurrentTimer {
    private ConcurrentTimer() { } // Noninstantiable

    public static long time(Executor executor, int concurrency,
                            Runnable action) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(concurrency);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown(); // Tell timer we're ready
                try {
                    start.await(); // Wait till peers are ready
                    action.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();  // Tell timer we're done
                }
            });
        }

        ready.await();     // Wait for all workers to be ready
        long startNanos = System.nanoTime();
        start.countDown(); // And they're off!
        done.await();      // Wait for all workers to finish
        return System.nanoTime() - startNanos;
    }
}
```

- 주의사항
    - 메서드에 넘겨진 실행자는 매개변수로 지정한 동시성 수준만큼 스레드를 생성할 수 있어야 한다.
    - 그렇지 않는다면 메서드가 끝나지 않는 상황이 오는데, 이를 스레드 기아 교착상태라 한다.
    - 시간을 재는 경우 System.nanoTime을 사용하자.

## [wait, notify](https://programmers.co.kr/learn/courses/9/lessons/278) 사용시 주의사항

- wait 메서드를 사용할 때는 반드시 대기 반복문 관용구를 사용하라.
    - 반복문 밖에서는 절대 호출하지 마라.
- 대기 전에 조건을 검사해 조건이 이미 충족되었다면, wait를 건너뛰게 한 것은 응답 불가 상태를 예방하는 조치다.
    - 만약 조건이 이미 충족되었는데 스레드가 notify 메서드를 먼저 호출한 후 대기 상태로 빠지면, 스레드를 다시 깨울 수 없다고 보장할 수 없다.
- 대기 후 조건을 검사하여 조건이 충족되지 않았다면 다시 대기하는 것은 안전 실패를 막는 조치이다.
    - 조건이 충족되지 않았는데 스레드가 동작을 이어가면 락이 보호하는 불변식을 깨뜨릴 위험이 있다.
    - 조건이 충족되지 않았는데 스레드가 깨어날 수 있는 상황
        - 스레드가 notify를 호출한 다음 대기 중이던 스레드가 깨어나는 사이, 다른 스레드가 락을 얻어 그 락이 보호하는 상태를 변경하는 경우
        - 조건이 만족되지 않았음에도 다른 스레드가 notify로 깨우는 경우
        - notifyAll을 호출하는 경우
        - 허위 각성
- notify vs notifyAll
    - notify로 깨우는 스레드는 랜덤
    - 다른 객체가 실수, 악의로 notify를 가로채는 경우, 영원히 대기하게 됨
