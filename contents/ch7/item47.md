# 반환 타입으로는 스트림보다는 컬렉션이 낫다.

자바 7 이전에는 원소 시퀀스를 반환하는 메서드의 반환 타입으로 Collection, Set, List 같은<br>
컬렉션 인터페이스, 혹은 Iterable이나 배열을 썼다.<br>
자바 8에 스트림이라는 개념이 추가되면서 이 선택이 복잡한 일이 되었다.<br><br>

원소 시퀀스를 반환하는 메서드는 당연히 스트림을 반환해야 한다는 말이 있지만 스트림은 for-each(향상된 for문)을 지원하지 않으므로<br>
무작정 스트림을 반환하는것은 좋지 않다. 스트림이 for-each 문을 사용하지 못하는 이유는 Stream이 iterable을 확장하지 않아서이다.

````java
List<Integer> list = Arrays.asList(1, 1, 2, 2, 3);
Stream<Integer> stream = list.stream();

// 에러
for(Integer num : stream::iterator){

}

// 정상적으로 작동하지만 지저분함
for(Integer num : (Iterable<Integer>)stream::iterator){

}
````

#### Stream<E>를 Iterable<E>로 중개해주는 어댑터
````java
public static <E> Iterable<E> iterableOf(Stream<E> stream){
    return stream::iterator;
}

for(Integer num : iterableOf(stream)){

}
````

#### Iterable<E>을 Stream<E>으로 중개해주는 어댑터
````java
public static <E> Stream<E> streamOf(Iterable<E> iterable){
    return StreamSupport.stream(iterable.spliterator(), false);
}
```` 

### Collection을 사용해서 스트림 파이프라인, 반복 모두 사용하는데 용이하게 하자.
오직 스트림 파이프라인만 사용할걸 안다면 반환 타입을 스트림으로, 오직 반복문에서만 쓰인다면 Iterable을 반환하는것이 좋지만,<br>
일반적으로 공개 API에서는 한 방식만 사용할거란 근거가 없기에 둘다 고려해야한다.<br>
Collection 인터페이스는 Iterable의 하위타입이므로 반복을 사용할 수 있고 stream()메서드를 통해 간단히 스트림 파이프라인을 사용할 수 있다.<br>

### Collection을 반환하는 예제
#### 입력 집합의 멱집합을 컬렉션에 담아 반환하는 메서드
````java
public class PowerSet {
    public static final <E> Collection<Set<E>> of(Set<E> s) {
        List<E> src = new ArrayList<>(s);
        if (src.size() > 30)
            throw new IllegalArgumentException(
                    "집합에 원소가 너무 많습니다(최대 30개).: " + s);
        return new AbstractList<Set<E>>() {
            @Override
            public int size() {
                return 1 << src.size();
            }

            @Override
            public boolean contains(Object o) {
                return o instanceof Set && src.containsAll((Set) o);
            }

            @Override
            public Set<E> get(int index) {
                Set<E> result = new HashSet<>();
                for (int i = 0; index != 0; i++, index >>= 1)
                    if ((index & 1) == 1)
                        result.add(src.get(i));
                return result;
            }
        };
    }

    public static void main(String[] args) {
        Set s = new HashSet(Arrays.asList(args));
        System.out.println(PowerSet.of(s));
    }
}
````

#### 입력 리스트의 모든 부분리스트를 스트림으로 반환하는 메서드
````java
public class SubLists {
    public static <E> Stream<List<E>> of(List<E> list) {
        return Stream.concat(Stream.of(Collections.emptyList()),
                prefixes(list).flatMap(SubLists::suffixes));
    }

    private static <E> Stream<List<E>> prefixes(List<E> list) {
        return IntStream.rangeClosed(1, list.size())
                .mapToObj(end -> list.subList(0, end));
    }

    private static <E> Stream<List<E>> suffixes(List<E> list) {
        return IntStream.range(0, list.size())
                .mapToObj(start -> list.subList(start, list.size()));
    }
}

//더욱 간결하지만 읽기는 좋지않은 코드
public static <E> Stream<List<E>> of(List<E> list) {    
    return IntStream.range(0, list.size())
        .mapToObj(start ->
            IntStream.rangeClosed(start + 1, list.size())
                .mapToObj(end -> list.subList(start, end)))
        .flatMap(x -> x);
}
````
위 코드를 반복으로 구현하면 코드는 더욱 지저분하지만 속도는 약 1.4배 가량 빠르다.<br>
반복문이 필요한 경우 아까 정의한 adapter를 통해 Stream을 Iterable로 변환 가능하지만 이는 2.3배가량 느리다.
 