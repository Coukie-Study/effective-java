# 이왕이면 제네릭 메서드로 만들라

클래스와 마찬가지로, 메서드도 제네릭으로 만들 수 있다.

제네릭 메서드는 매개변수 타입과 리턴 타입으로 타입파라미터를 갖는 메소드를 말한다.

제네릭 메서드 선언 방법

- 리턴 타입 앞에 "< >" 기호를 추가하고 타입 파라미터를 기술한다.
- 타입 파라미터를 리턴타입(Box<T>)과 매개변수(T)에 사용한다.



### 로타입 사용 -안티패턴

```java
public static Set union(Set s1, Set s2){
        Set result=new HashSet(s1);
        result.addAll(s2);
        return result;
    }
```

컴파일은 되지만 경고가 발생한다.

경고를 없애기 위해서는 이 메서드를 타입 안전하게 만들어야 한다.



### 제네릭 메서드

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2){
        Set<E> result=new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }
```

이 메서드는 경고 없이 컴파일 되며, 타입 안전하고, 쓰기도 쉽다.	



### 제네릭 싱글턴 팩터리

때때로 불변 객체를 여러 타입으로 활용할 수 있게 만들어야 할 때가 있다.

제네릭은 런타임에 타입 정보가 소거되므로 하나의 객체를 어떤 타입으로든 매개변수화 할 수 있다.

하지만 이렇게 하려면 요청한 타입 매개변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리를 만들어야 한다.

이 패턴을 제네릭 싱클 팩터리라고 한다.

이번에는 항등함수를 담은 클래스를 만들어 보자.

항등함수란 쉽게 말해 T인자를 받으면 T객체를 반환하는 함수형 인터페이스이다.

```java
private static UnaryOperator<Object> IDENTITY_FN = (t)->t;
//하나의 인자와 리턴타입을 가진다. 인자의 타입과 리턴타입이 같아야 한다.
    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction(){
        return (UnaryOperator<T>) IDENTITY_FN;
    }
```

IDENTITY_FN을 UnaryOperator<T>로 형변환하면 경고가 발생한다.

T가 어떤 타입이든 UnaryOperator<Object>는 UnaryOperator<T>가 아니기 때문이다.

하지만 항등함수란 입력 값을 수정 없이 그대로 반환하는 특별한 함수이므로 T가 어떤 타입이든 UnaryOperator<T>를 사용해도 타입 안전하다.



### 재귀적 타입 한정

드물긴 하지만 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용범위를 한정할 수 있다.

재귀적 한정 타입은 주로 타입의 자연적 순서를 정하는 Comparable 인터페이스와 함께 쓰인다.

```java
public interface Comparable<T>{
	int compareTo(T o);
}
```

여기서 타입 매개변수 T는 Comparable<T>를 구현한 타입이 비교할 수 있는 원소의 타입을 정의한다.

실제로 거의 모든 타입은 자신과 같은 타입의 원소와만 비교할 수 있다.

Comparable을 구현한 원소의 컬렉션을 입력받는 메서드들은 주로 그 원소들을 정렬 혹은 검색하거나, 최솟값이나 최댓값을 구하는 식으로 사용된다.

```java
public static <E extends Comparable<E>> E max(Collection<E> c) {
  			//<E extends Comparable<E>>는 "모든 타입 E는 자신과 비교할 수 있다"라고 읽을 수 있다.
        if (c.isEmpty())
            throw new IllegalArgumentException("컬렉션이 비어있다.");

        E result = null;
        for (E e : c)
            if (result == null || e.compareTo(result) > 0)
                result = Objects.requireNonNull(e);

        return result;
    }
```

























