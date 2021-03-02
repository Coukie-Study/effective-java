# 스트림에는 부작용 없는 함수를 사용하라.
스트림은 필요한 계산을 일련의 변환으로 재구성하는 것이므로 각 변환 단계는 가능한 직전 단계의 결과를 받아 처리하는 순수 함수여야 한다.
<br> 순수 함수란 오직 입력(함수의 변수)만이 결과에 영향을 주는 함수를 말한다.

#### 스트림처럼 보이지만 스트림이 아닌 코드
````java
Map<String, Long> freq = new HashMap<>();
try (Stream<String> words = new Scanner(file).tokens()) { 
    words.forEach(word -> {
        freq.merge(word.toLowerCase(), 1L, Long::sum);
    });
}
````
#### 스트림을 제대로 사용한 코드
````java
Map<String, Long> freq;
try (Stream<String> words = new Scanner(file).tokens()) {
    freq = words
        .collect(groupingBy(String::toLowerCase, counting()));
}
````
forEach 연산은 종단 연산중 기능이 제일 적고 가정 덜 스트림답다.<br>
그러므로 forEach 연산은 스트림 계산 결과를 보고할 때만 사용하고, 계산을 할때는 사용하지 말자.

### Collectors
Collectors는 Stream을 사용할때 꼭 필요한 새로운 개념으로 종단연산 collect의 인수로 일반적으로 Collection, Map 을 반환하는 역할을 한다.
<br> Collection을 반환하는 메서드로는 toList(), toSet(), toCollection(collectionFactory)가 있다.

````java
List<String> topTen = freq.keySet().stream()
    .sorted(comparing(freq::get).reversed())
    .limit(10)
    .collect(toList());
````
#### Map을 반환하는 Collectors 메서드
- toMap(keyMapper, valueMapper)
````java
private static final Map<String, Operation> stringToEnum = 
    Stream.of(valus()).collect(
        toMap(Object::toString, e -> e));
````
- toMap(keyMapper, valueMapper, mergeFunction)
````java
//예제 1
List<Integer> list = Arrays.asList(1, 1, 2, 2, 3);
Map<Integer,Integer> map = list.stream().collect(
    Collectors.toMap(e -> e,e -> e, Integer::sum));
//결과 : [2, 4, 3]
System.out.println(map.values());

//예제 2
Map<Artist, Album> topHits = albums.collect(
    toMap(Album:artist, a->a, maxBy(comparing(Album::sales))));
````
- toMap(keyMapper, valueMapper, mergeFunction, mapSupplier)

3가지 인수를 받는 toMap에 반환할 Map의 구현체를 지정할 수 있게하는 인수가 추가됨.

- groupingBy
````java
//Map<String, List<String>
words.collect(groupingBy(word) -> alphabetize(word))
//Map<String, Set<String>
words.collect(groupingBy(word) -> alphabetize(workd), toSet())

//Map<String, Long>
Map<String, Long> freq = words
    .collect(groupingBy(String::toLowerCase, counting()));
````
- groupingByConcurrent

ConcurrentHashMap 인스턴스를 반환한다.

- partitioningBy
````java
List<Integer> list = Arrays.asList(1, 1, 2, 2, 3);
Map<Boolean,List<Integer>> map = list.stream().collect(Collectors.partitioningBy(num -> num > 2));
//결과 : [1, 1, 2, 2]
System.out.println(map.get(false));
//결과 : [3]
System.out.println(map.get(true));
````

#### 다운스트림 Collectors
- counting
- summing
- averaging
- summarizing
- filtering
- mapping
- flatMapping
- collectingAndThen

#### minBy, maxBy
````java
List<Integer> list = Arrays.asList(1, 1, 2, 2, 3);
Optional<Integer> min = list.stream().collect(minBy(Integer::compareTo));
//결과 : 1
System.out.println(min.get());
````

#### joining
````java
List<String> list = Arrays.asList("Hi", "Bye");
String str = list.stream().collect(joining());
//결과 : HiBye
System.out.println(str);
````
