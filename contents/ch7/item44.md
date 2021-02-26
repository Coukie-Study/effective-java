# 표준 함수형 인터페이스를 사용하라

자바 8부터는 람다 지원을 위해 java.util.function 패키지에 표준 함수형 인터페이스를 다양하게 제공하고 있다.

필요한 용도에 맞는게 있다면, 직접 구현하지 말고 표준 함수형 인터페이스를 활요하는 것이 좋다.

관리할 대상도 줄어들고 많은 유용한 디폴트 메서드가 부담을 덜어준다.

java.util.function패키지에는 총 43개의 인터페이스가 담겨있다. 이 중에 기본 인터페이스 6개만 기억하면 나머지를 충분히 유추할 수 있다.

#### UnaryOperator< T >

- T apply(T t): 반환값과 인수의 타입이 같은 함수, 인수는 1개

​       예) String::toLowerCase

#### BinaryOperator< T >

- T apply(T t1, T t2): 반환값과 인수의 타입이 같은 함수, 인수는 2개

  예) BigInteger::add

#### Predicate< T >

- boolean test(T t): 한 개의 인수를 받아서 boolean을 반환하는 함수

  예) Collection::isEmpty

#### Function<T,R>

- R apply(T t): 인수와 반환 타입이 다른 함수

  예) Arrays::asList

#### Supplier< T >

- T get(): 인수를 받지 않고 값을 반환, 제공하는 함수

  예) Instant::now

#### Consumer< T >

- void accept(T t): 한 개의 인수를 받고 반환값이 없는 함수

  예) System.out::println

표준 함수형 인터페이스 대부분은 기본 타입만 지원한다. 그렇다고 기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하지는 말자. 동작은 하지만 계산량이 많아지는 경우 성능이 매우 느려질 수 있다.



### 코드를 직접 작성할 때

```java
//Comparator
public interface Comparator<T>{
	int compare(T o1,T o2);
}

//ToIntBiFunction
public interface TointBiFunction<T, U>{
  int applyAsInt(T t,U u);
}
```

두 개의 함수형 인터페이스는 구조적으로 같다.

인자 두개를 받아서 정수형을 반환하는 함수이다.

#### Comparator가 독자적인 인터페이스로 살아남아야 하는 이유

- API에서 굉장히 자주 쓰이며, 이름이 그 용도를 명확하게 설명해준다
- 구현하는 쪽에서 따라야 하는 규약이 있다.
- 비교자들을 변환하고 조합해주는 유용한 디폴트 메서드들을 담고 있다.

이 중 하나 이상을 만족한다면 전용 함수형 인터페이스를 구핸해야 할지 고민해야 한다.



### 직접 만든 함수형 인터페이스에는 항상 @FunctionalInterface 애너테이션을 사용하라

- 해당 클래스의 코드나 설명 문서를 읽은 이에게 그 인터페이스가 람다용으로 설계된 것임을 알려준다
- 해당 인터페이스가 추상 메서드를 오직 하나만 가지고 있어야 컴파일 되게 해준다.
- 유지보수 과정에서 누군가 실수로 메서드를 추가하지 못하게 막아준다.







