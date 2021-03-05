# 스트림 병렬화는 주의해서 적용하라

- 자바8에서부터 도입된 stream은 parallel 메서드를 호출하면 자동으로 파이프라인을 병렬 실행할 수 있는 스트림을 지원
- 다만 다른 병렬 프로그래밍 방식과 같이 올바르게 작성하기 위해 주의할 점이 있다.

- 다음과 같이 코드를 실행하면 아무런 결과를 가져오지 못함(hang)

```java
public class ParallelMersennePrimes {
    public static void main(String[] args) {
        primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
                .parallel()
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(20)
                .forEach(System.out::println);
    }

    static Stream<BigInteger> primes() {
        return Stream.iterate(TWO, BigInteger::nextProbablePrime);
    }
```

- 이 코드는 스트림 라이브러리가 파이브라인을 병렬화하는 방법을 찾아내지 못했기 때문이다.
- Stream.iterate거나 중간 연산으로 limit를 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다.
    - Stream.iterate인 경우, 병렬로 실행하기 위해서 데이터 소스를 chunk 단위로 자르는데, iterate는 순차적으로 다음 요소를 반환하는 방식이라 chunk 단위로 자르기 어렵다.
    - limit의 경우 cpu 코어가 남는다면 원소를 몇 개 더 처리한 후 제한된 개수 이후의 결과를 버려도 아무런 해가 없다고 가정하기 때문에 자칫 버리는 연산으로 인해 성능이 더 나빠질 수 있다. 이런 소수 연산의 경우 뒷 연산이 앞 연산의 합보다도 커지기 때문에 특히 주의해야 한다.
- 즉 스트림 파이프라인을 마구잡이로 병렬화하면 안 된다.

## 병렬화 시 좋은 소스

- 대체로 스트림의 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int range, long range일 때 병렬화의 효과가 가장 좋다.
    - 이 자료구조의 특징은 모두 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어 다수의 스레드에 일을 분배하기 좋다는 특징이 있다.
    - 또한 [참조 지역성](https://jwprogramming.tistory.com/18)이 뛰어나다는 것이 있다. (이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻)
        - 참조 지역성이 나쁘면 스레드는 데이터가 주 메모리에서 캐시 메모리로 전송되어 오기를 기다린다.
        - 따라서 참조 지역성은 다량의 데이터를 처리하는 벌크 연산을 병렬화할 때 중요한 요소이다.
    - 나누는 작업은 [spliterator](https://jistol.github.io/java/2019/11/17/spliterator/)가 담당하며 Stream이나 Iterable의 spliterator로 얻어올 수 있다.

## 종단 연산

- 스트림 파이프라인의 종단 연산 동작 방식 역시 병렬 수행 효율에 영향을 준다.
- 종단 연산에서 수행하는 작업량이 파이프라인 전체 작업에서 상당 비중을 차지하고, 순차적인 연산이라면 파이프라인 병렬 수행 효과는 제한되기 때문
- 종단 연산 중 가장 적합한 것은 축소다.
    - reduce, min, max, count, sum
- anyMatch, allMatch, noneMatch처럼 조건에 맞으면 반환되는 메서드도 병렬화에 적합
- 다만 collect는 컬렉션을 합치는 부담이 크기 때문에 적합하지 않다.

## 그 밖의 주의할 점

- 직접 구현한 Stream, Iterable, Collection이 병렬화의 이점을 제대로 누리게 하고 싶다면 spliterator 메서드를 반드시 재정의하고 성능을 테스트해라
- 스트림을 잘못 병렬화하면 성능이 나빠질 뿐만 아니라 결과 자체가 오동작(safety failure)할 수 있다.
    - safety failure는 병렬화한 파이브라인이 사용하는 함수 객체가 명세대로 동작하지 않을 때 벌어질 수 있다.
    - stream에서 정의한 함수 객체에 대한 규약을 지키지 않을 경우, 특히 병렬 처리 시에 심각한 실패를 가져올 수 있다.
- 위의 모든 것을 고려한다 하더라도 병렬화에 필요한 비용을 효율성이 상쇄하지 못한다면 성능 향상을 미미하다.
    - 따라서 스트림을 통해 성능을 향상 시키고 싶다면 최소 원소 수 X 원소당 수행되는 코드가 수십만 줄일 때 실행해봄직 하다.
- 병렬화는 성능 최적화 수단이다. 따라서 다른 최적화와 마찬가지로 변경 전후 성능 테스트를 통해 사용 가치를 측정해야 한다.
- 위의 모든 것을 고려했을 때 실질적으로 병렬화를 사용할 일은 적다. 하지만 제대로 사용했을 경우, 성능 향상을 누릴 수 있다.

```java
public class ParallelPrimeCounting {
    // Prime-counting stream pipeline - parallel version (Page 225)
    static long pi(long n) {
        return LongStream.rangeClosed(2, n)
                .parallel()
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }
}
```
