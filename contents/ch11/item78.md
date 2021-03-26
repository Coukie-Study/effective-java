# 공유 중인 가변 데이터는 동기화해 사용하라

## synchronized

- 해당 키워드가 쓰여진 메서드나 블록을 한번에 한 스레드씩 수행하도록 보장한다.
- 이를 배타적 실행, 즉 한 스레드가 변경하는 중이라 상태가 일관되지 않은 순간의 객체를 다른 스레드가 보지 못하게 막는 용도로만 생각한다.
    - 다시 말해 객체를 하나의 일관된 상태에서 다른 일관된 상태로 변화시킨다.
    - 동기화를 제대로 사용하면 일관되지 않은 순간을 볼 수 없다.
- 위 기능과 더불어 중요한 기능이 하나 더 있는데, 동기화 없이는 한 스레드가 만든 변화를 다른 스레드에서 확인하지 못할 수 있다.
    - 동기화는 동기화된 메서드나 블록에 들어간 스레드가 같은 락의 보호하에 수행된 모든 이전 수정의 최종 결과를 보게 해준다.
- 언어 명세상 long과 double 외의 변수를 읽고 쓰는 동작은 원자적이다.
    - 원자적 연산 : 중단이 불가능한 연산(ex. 1. 읽고, 2. 쓰기)(아닌 예시 : ++ 연산)
    - long과 double이 원자적이지 않은 이유
        - 64비트라 32비트씩 끊어서 연산이 이루어지기 때문에
- 동기화는 배타적 실행뿐 아니라 스레드 사이의 안정적인 통신에 꼭 필요하다.
    - 위의 설명만 보면 long과 double을 제외한 다른 원시 타입은 동기화가 불필요하다고 생각할 수 있지만 사실 필요하다.
    - 한 스레드가 저장한 값이 다른 스레드에게 보이는가는 보장하지 않는다.
    - 스레드가 필드를 읽을 때 항상 수정이 완전히 반영된 값을 얻는다고 보장하지만, 한 스레드가 저장한 값이 다른 스레드에게 보이는가는 보장하지 않는다
    - [https://www.baeldung.com/java-volatile](https://www.baeldung.com/java-volatile)
- 공유 중인 가변 데이터를 비록 원자적으로 읽고 쓸 수 있더라도 동기화에 실패하면 결과가 좋지 않다.

```java
public class StopThread {
    private static boolean stopRequested;

    public static void main(String[] args)
            throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested)
                i++;
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```

- 동기화하지 않아 메인 스레드가 수정한 값을 백그라운드 스레드가 언제쯤 볼 수 있을 지 보장할 수 없다.
- stopRequested 필드를 동기화해 접근하면 이 문제를 해결할 수 있다.

```java
public class StopThread {
    private static boolean stopRequested;

    private static synchronized void requestStop() {
        stopRequested = true;
    }

    private static synchronized boolean stopRequested() {
        return stopRequested;
    }

    public static void main(String[] args)
            throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested())
                i++;
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        requestStop();
    }
}
```

- 쓰기 및 읽기 메서드 모두 동기화했음에 주의
    - 쓰기와 읽기 모두 동기화하지 않으면 동작이 보장되지 않음

## volatile

- 필드에 위 선언을 통해 동기화를 생략 가능
- 배타적 수행과는 관계가 없지만, 가장 최근에 기록된 값을 읽게 됨을 보장

```java
public class StopThread {
    private static volatile boolean stopRequested;

    public static void main(String[] args)
            throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested)
                i++;
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```

- 다만 증가 연산자처럼(++) 원자적이지 않은 연산(이 연산은 읽고 쓰는 연산 2개가 있다.)은 기존처럼 동기화가 필요하다.

```java
private static volatile int nextSerialNumber = 0;

public static int generateSerialNumber() {
    return nextSerialNumber++;
}
```

- 만약 두 스레드가 있고 첫번째 스레드가 읽고 쓰는 연산을 하는 도중, 두번째 스레드가 연산에 끼어든다면 잘못된 결과를 도출(같은 값을 받게 된다.)한다. 이러한 오류를 안전 실패라 한다.
- synchronized 한정자를 붙이고 기존 volatile을 제거

## Atomic class

- AtomicXXX는 [CAS(compare and swap)](https://javaplant.tistory.com/23)를 통해 원자성과 안정적 통신을 지원한다.
- 성능도 기존 동기화보다 우수하다.

```java
private static final AtomicLong nextSerialNumber = new AtomicLong();

public static int generateSerialNumber() {
    return nextSerialNumber.getAndIncrement();
}
```

## 불변 객체를 써라

- 가장 좋은 방법은 가변 객체를 공유하지 않는 것.
- 가변 데이터는 단일 스레드에서만 쓰도록 하자.
- 한 스레드가 데이털르 다 수정한 후, 다른 스레드에 공유할 때 해당 객체에서 공유하는 부분만 동기화해도 된다.
    - 이런 객체를 사실상 불변이라 하고, 다른 스레드에 이런 객체를 건네는 행위를 안전 발행이라 한다.
- 객체를 안전하게 발행하는 방법
    - 정적 필드, volatile 필드, final 필드, 보통의 락을 토앻 접근하는 필드에 저장, 동시성 컬렉션도 고려 대상, volatile 필드, final 필드, 보통의 락을 토앻 접근하는 필드에 저장, 동시성 컬렉션도 고려 대상
