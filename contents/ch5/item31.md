# 한정적 와일드카드를 사용해 API 유연성을 높이라

매개변수화 타입은 불공변(invariant)이다.

불공변(invariant)는 상속 관계에 상관없이 자기 타입만 허용하는 것을 말한다.

List< Object >에 뭐든 넣을 수 있지만, List< String >에는 문자열만 들어가므로 List< Object >의 하위타입이 될 수 없다.(리스코프 치환 원칙에 어긋남)

불공변 방식보다 유연함이 필요할 때 한정적 와일드카드 타입을 이용한다.



### 생산자, 소비자

생산자: 입력 매개변수의 원소를 컬렉션 인스턴스로 옮겨 담는다는 뜻(write)

- 매개변수로 받은 리스트 원소를 pushAll 메서드를 가지는 스택에 옮겨 담음

소비자: 입력 매개변수로 컬렉션 인스턴스의 원소를 옮겨 담는다는 뜻(read)

- popAll 메서드를 가지는 스택의 원소를 매개변수로 받은 리스트에 옮겨 담음



### 생산자 매개변수에 와일드카드 타입 적용

```java
//와일드카드 타입을 사용하지 않은 pushAll메서드
public void pushAll(Iterable<E> src){
	for(E e: src){
    push(e);
  }
}

Stack<Number> numberStack = new Stack<>();
Iterable<Integer> integers=...;
numberStack.pushAll(integers);	//매개변수화 타입이 불공변이므로 오류 발생
```

```java
//생산자 매개변수에 와일드카드 타입 적용
public void pushAll(Iterable<? extends E> src) {
  //pushAll의 입력 매개변수 타입은 'E의 Iterable'이 아니라 'E의 하위 타입의 Iterable'이어야 한다.
	for (E e : src) {
		push(e);
	}
}
```



### 소비자 매개변수에 와일드카드 타입 적용

```java
//와일드카드 타입을 사용하지 않은 popAll메서드
public void popAll(Collection<E> destination) {
	while (!isEmpty()) {
		destination.add(pop());
	}
}

Stack<Number> numberStack = new Stack<>();
Collection<Object> objects = ...;
numberStack.popAll(objects);
```

Collection< Object >가 Collection< Number >의 하위 타입이 아니기 때문에 문제가 발생한다.

'E의 Collection'이 아니라 'E의 상위 타입의 Collection'이어야 한다.

```java
//소비자 매개변수에 와일드카드 타입 적용
public void popAll(Collection<? super E> destination) {
	while (!isEmpty()) {
		destination.add(pop());
	}
}
```



유연성을 극대화 하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라

하지만 입력 매개변수가 생산자와 소비자 역할을 동시에 한다면 와일드사용하면 안된다.



### 펙스(PECS): producer-extends, consumer-super

- 생산자라면 <? extends T>를 사용한다.
- 소비자라면 <? super T>를 사용한다.



### 조금 더 복잡한 예제

```java
public static <E extends Comparable<E>> E max(List<E> list)
```

위의 max메서드는 와일드 카드 타입을 사용해 다음과 같이 고칠 수 있다.

```java
public static <E extends Comparable<? super E>> E max(List<? extends E> list)
```

이번에는 PECS 공식을 두 번 적용했다. 

입력 매개변수에서는 E 인스턴스를 생산하므로 원래의 List<E>를 List<? Extends E>로 수정했다.

원래 선언에서는 E가 Comparable<E>를 확장한다고 정의했는데, 이때 Comparable<E>는 E 인스턴스를 소비한다.(그리고 선후관계를 뜻하는 정수를 생산한다.)

Comparable은 언제나 소비자 이므로, 일반적으로 Comparable<E> 보다는 Comparable<? super E>를 사용하는편이 낫다.



### 타입 매개변수와 와일드카드, 둘 중 어느 것을 사용해도 괜찮은 경우

```java
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```

주어진 리스트에서 명시한 두 인덱스의 아이템들을 교환(swap)하는 정적 메서드를 두 방식 모두로 정의한 것이다.

public API라면 간단한 두 번째가 낫다.

기본규칙은 이렇다. 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체하라.

```java
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```

하지만 이 swap 선언에는 문제가 하나 있는데 구현한 코드가 컴파일되지 않는다는 것이다.

List<?>에는 null 외에는 어떤 값도 넣을 수 없다는 데 있다.

이를 해결하기 위해서는 와일드카드 타입의 실제 타입을 알려주는 메서드를 private 도우미 메서드로 따로 작성하여 활용하는 방법이다

```java
public static void swap(List<E> list, int i, int j) {
    swapHelper(list, i, j);
}

// 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```

swapHelper 메서드는 리스트가 List<E>임을 알고 있다. 즉 이 리스트에서 꺼낸 값의 타입은 항상 E이고, E 타입의 값이라면 이 리스트에 넣어도 안전하다.













