# 옵셔널 반환은 신중히 하라

자바 8 전에는 메서드가 특정 조건에서 값을 반환할 수 없을 때 취할 수 있는 선택지가 두 가지 있었다.

- 예외를 던진다.
- null을 반환한다.

예외는 진짜 예외적인 상황에서만 사용해야하고 예외를 생성할 때 스택 추적 전체를 캡처하므로 비용도 만만치 않다.

null을 반환하면 이런 문제가 생기지 않지만 또 다른 문제가 있다. null을 반환할 수 있는 메서드를 호출할 때는 별도의 null처리 코드를 추가 해야한다. null 처리를 무시하고 반환된 null 값을 어딘가에 저장해두면 언젠가 NullPointerException이 발생할 수 있다.

### Optional< T >

- Optional< T >는 null이 아닌 T타입 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.

- 아무것도 담지 않은 옵셔널은 '비었다'고 하고 어떤 값을 담은 옵셔널은 '비지 않았다'고 한다.
- 옵셔널은 원소를 최대 1개 가질 수 있는 '불변' 컬렉션이다.



### Optional< T >를 사용하는 경우

- 보통은 T를 반환해야 하지만 특정 조건에서는 아무것도 반환하지 않아야 할 때 T 대신 Optional< T >를 반환하도록 선언하면 된다.
- 유효한 반환값이 없을 때는 빈 결과를 반환하는 메서드가 만들어 진다.
- 옵셔널을 반환하는 베서드는 예외를 던지는 메서드보다 유연하고 사용하기 쉬우며, null을 반환하는 메서드보다 오류 가능성이 작다.

```java
//컬렉션에서 최댓값을 구한다.
public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty())
            throw new IllegalArgumentException("빈 컬렉션");

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0)
                result = Objects.requireNonNull(e);
        }
        return result;
    }
```

이 메서드에 빈 컬렉션을 건네면 IllegalArgumentException을 던진다.

Optional< E >를 반환하는 코드로 수정해보자.

```java
 public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        if (c.isEmpty())
            return Optional.empty();

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0)
                result = Objects.requireNonNull(e);
        }
        return Optional.of(result);
    }
```

빈 옵셔널은 Optional.empty()로 만들고, 값이 든 옵셔널은 Optional.of(result)로 생성했다.

null값도 허용하는 옵셔널을 만들려면 Optional.ofNullable(result)를 사용하면 된다.

옵셔널을 반환하는 메서드에서는 절대 null을 반환하지 말자.

### 옵셔널은 Checked Exception과 취지가 비슷하다

- 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려준다. 
- Unchecked Exception을 던지거나 null을 반환한다면 API 사용자가 그 사실을 인지 하지 못해 끔찍한 결과로 이어질 수 있다.
- 하지만 Checked Exception을 던지면 클라이언트에서는 반드시 이에 대처하는 코드를 작성해 넣어야 한다.
- 비슷하게 메서드가 옵셔널을 반환한다면 클라이언트는 값을 받지 못했을 때 취할 행동을 선택해야 한다. 

```java
//기본값 설정
String lastWordInLexicon = max(words).orElse("단어 없음...");
```

```java
//예외를 던짐
Toy myToy = max(toys).orElseThrow(TemperTantrumException::new);
```

```java
//항상 값이 채워져 있다고 가정한다.
Element lastNobleGas = max(Elements.NOBLE_GASES).get();
```



### 컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안된다.

- 반환값으로 옵셔널을 사용한다고 해서 무조건 득이 되는건 아니다.

- 빈 Optional<List< T >>를 반환하기 보다는 빈 List< T >를 반환하는게 좋다.
- 빈 컨테이너를 그대로 반환하면 클라이언트에 옵셔널 처리 코드를 넣지 않아도 된다.



### 반환타입을 T대신 Optional< T >로 선언하는 경우

- 결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 Optional< T >를 반환한다.
- Optional도 엄연히 새로 할당하고 초기화해야 하는 객체이다. 그래서 성능이 중요한 상황에서는 옵셔널이 맞지 않을 수 있다.

### 박싱된 기본 타입을 담는 옵셔널

- 박싱된 기본 타입을 담는 옵셔널은 기본 타입 자체보다 무거울 수밖에 없다. 값을 두 겹이나 감싸기 때문이다.

- 그래서 자바 API 설계자는 int, long, double 전용 옵셔널 클래스들을 준비해놨다.
- OptionalInt, OptionalLong, OptionalDouble
- 이 옵셔널들도 Optional< T >가 제공하는 메서드를 거의 다 제공한다.
- 따라서 박싱된 기본 타입을 담은 옵셔널을 반환하는 일은 없도록 해야한다.



### 정리

- 값을 반환하지 못할 가능성이 있고, 호출할 때마다 반환값이 없을 가능성을 염두에 둬야하는 메서드라면 옵셔널을 반환해야 할 상황일 수 있다.
- 옵셔널 반환에는 성능 저하가 뒤따르니, 성능에 민감한 메서드라면 null을 반환하거나 예외를 던지는 편이 나을 수도 있다.
- 옵셔널을 반환값 이외의 용도로 쓰는 경우는 매우 드물다.









