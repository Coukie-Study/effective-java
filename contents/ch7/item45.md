# 스트림은 주의해서 사용하라.
### 스트림(Stream) 이란?
다량의 데이터 처리 작업을 돕고자 자바 8에 추가된 API.
````java
List<Integer> list = Arrays.asList(1, 7, 5, 2);
Stream<Integer> stream = list.stream();
stream.sorted().limit(3).forEach(System.out::println);
````

### 스트림 핵심 추상 개념
1. 스트림은 데이터 원소의 유한 혹은 무한 시퀀스를 뜻한다.
2. 스트림 파이프라인은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다.

### 스트림의 원소들
- 컬렉션, 배열, 파일, 정규표현식 패턴 매처, 난수 생성기, 다른 스트림
- int, long, double

### 스트림 파이프라인
- 스트림 파이프라인은 소스 스트림에서 시작해 종단 연산으로 끝나며, 그 사이에 하나 이상의 중간 연산이 있을 수 있다.
- 각 중간 연산은 스트림을 어떠한 방식으로 변환한다.
- 스트림 파이프라인은 지연평가(lazy evaluation)되므로 종단 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않아 무한 스트림을 다룰 수 있게해준다.
- 연쇄를 지원하는 fluent API 이므로 단 하나의 표현식으로 완성할 수 있다.
- 기본적으로 파이프라인은 순차적으로 수행되며, parallel 메서드를 호출해서 병렬로 수행할 수 있다.

### char 스트림은 지원하지 않는다. 
````java
//chars()의 반환형은 IntStream 이다.
"Hello world".chars().forEach(System.out::print);
"Hello world".chars().forEach(x -> System.out.println((char) x));
````

### 기존 코드를 스트림을 사용하면 좋을때만 리팩터링 하자.
스트림 파이프라인은 주로 람다나 메서드 참조로 코드를 구성한다.<br>
반면 반복 코드(스트림을 사용하지 않은 기존의 반복 코드)는 코드 블록을 사용한다.
#### 람다와 코드 블록의 차이점
- 코드 블록에서는 범위안의 지역변수를 읽고 수정할 수 있다. 람다에서는 final이거나 사실상 final인 변수만 읽을 수 있다. 즉 지연 변수 수정이 불가능하다.
- 코드 블록에서는 return, break, continue를 사용할 수 있다.

#### 스트림을 사용하기 좋은 경우
1. 원소들의 시퀀스를 일관되게 변환한다.
2. 원소들의 시퀀스를 필터링한다.
3. 원소들의 시퀀스를 하나의 연산을 사용해 결합한다.
4. 원소들의 시퀀스를 컬렉션에 모은다.(공통된 속성을 기준으로 묶어가며)
5. 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.

#### 스트림을 사용하기 안좋은 경우
한 데이터가 스트림 파이프라인의 여러 단계를 통과할 때, 이 데이터의 각 단계에서의 값들에 동시에 접근하는 경우<br>
단계가 넘어갈때 마다 그 전의 값은 잃는 구조이기 때문에 스트림을 사용하기 까다롭다.

````java
static Stream<BigInteger> primes(){
    return Stream.iterate(TWO, BigInteger::nextProbablePrime);
}

public static void main(String[] args){
    primes().map(p -> TWO.pow(p.intValueExact()).subTract(ONE))
        .filter(mersenne -> mersenne.isProbablePrime(50))
        .limit(20)
        .forEach(System.out::println);
}

.forEach(mp -> System.out.println(mp.bitLength() + ": " + mp));
````

#### 스트림과 반복중 어느 쪽을 써야할지 알기 어려운 경우
````java
//반복으로 구현
private static List<Card> newDeck(){
    List<Card> result = new ArrayList<>();
    for (Suit suit : Suit.values()){
        for(Rank rank : Rank.values()){
            result.add(new Card(suit, rank));
        }
    }
}

//스트림으로 구현
private static List<Card> newDeck(){
    return Stream.of(Suit.values())
        .flatMap(suit ->
            Stream.of(Rank.values())
                .map(rank -> new Card(suit, rank)))    
        .collect(toList());
}
````
확신하기 어려운 경우 둘다 사용해보고 더 나은 쪽을 택하는것이 좋다.
 
 