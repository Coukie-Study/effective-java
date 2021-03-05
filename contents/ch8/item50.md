# 적시에 방어적 복사본을 만들라

## 방어적 복사의 필요성

- 자바가 타 언어에 비해 안전한 언어라 할지라도 클라이언트가 불변식을 깨뜨릴 수 있다는 것을 가정하고 방어적인 프로그래밍을 해야 한다.

```java
public final class Period {
    private final Date start;
    private final Date end;

    /**
     * @param  start the beginning of the period
     * @param  end the end of the period; must not precede start
     * @throws IllegalArgumentException if start is after end
     * @throws NullPointerException if start or end is null
     */
    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0)
            throw new IllegalArgumentException(
                    start + " after " + end);
        this.start = start;
        this.end   = end;
    }
}
```

- 어떤 객체든 그 객체 내의 접근 권한을 넘어서 내부를 수정하는 일은 불가능하다. 하지만 주의를 기울이지 않는다면 내부를 수정하도록 할 수 있다.

```java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
end.setYear(78); // 변경 발생
```

- 위의 경우에는 Date API를 쓰는게 아니라 새롭게 도입된 LocalDateTime을 쓴다면 불변 객체를 쓸 수 있기에 내부를 수정할 수 없다.
- 이처럼 가변 객체를 직접 넣어준다면 불변식이 쉽게 깨질 수 있다. 따라서 생성자에서 받은 가변 매개변수를 각각 방어적으로 복사해야 한다.

```java
public Period(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end   = new Date(end.getTime());

    if (this.start.compareTo(this.end) > 0)
        throw new IllegalArgumentException(this.start + " after " + this.end);
}
```

- 여기서 주의할 점은, 원본의 유효성을 검사한 다음, 복사를 할 것으로 생각하겠지만, 반대로 해야 한다.
    - 멀티스레드 환경에서, 원본 객체의 유효성을 검사한 후 복사본을 만드는 순간에 원본 객체의 값이 변할 수 있기 때문이다.(Time-Of-Check/Time-Of-User 공격)
- 또한 방어적 복사를 수행할 때 clone을 사용하지 않았는데, 그 이유는 Date가 final class가 아니기 때문에 clone을 재정의할 수 있게 되고 악의를 가진 하위 클래스에서 이를 활용할 수 있기 때문이다.
    - 따라서 매개변수가 제 3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용하면 안된다.

- 또한 생성자 뿐만 아니라 접근자 메서드를 통해 내부의 가변 정보를 직접 드러낼 때도 고려해야 한다.

```java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
p.end().setYear(78); // 변경 발생
```

- 이 경우는 단순히 가변 필드의 방어적 복사본을 반환하자

```java
public Date start() {
    return new Date(start.getTime());
}

public Date end() {
    return new Date(end.getTime());
}
```

- 생성자, 접근자 방어적 복사를 통해 클래스는 완벽하게 불변 객체로 활용할 수 있다. 불변식을 위배할 수 없게 되며, 가변 필드에 접근할 수 없다.
- 위의 경우는 생성자와 달리 clone을 활용해도 된다.
    - 전달하는 객체가 신뢰할 수 없는 하위 클래스가 아니기 때문
    - 다만 일반적인 이유 때문에(item 13) 생성자나 정적 팩터리 메서드를 활용하자

 

- 변경될 수 있는 객체가 내부에 자료구조에 보관해야 할 때면 그 객체가 임의로 변경되도 되는지 확인해야 한다. 만약 변경시 문제가 발생한다면 방어적 복사를 수행해라
- 마찬가지로 클라이언트에게 객체를 전달하는 경우도 상황을 고려해서 방어적 복사가 필요할 경우 수행해야한다. 내부에 배열을 반환하는 경우는 가변임을 고려할 때 방어적 복사를 수행해야 한다.
- 되도록 불변 객체를 쓰는 것이 방어적 복사를 줄일 수 있는 방법이다.

- 방어적 복사는 성능 저하가 뒤따르고, 항상 쓸 수 있는 것도 아니다.
    - ex) 같은 패키지에 속하는 경우 클라이언트가 내부 상태를 변경하지 않는 것이 확실할 경우 방어적 복사 필요 X
- 호출자가 컴포넌트 내부를 수정하지 않는 것이 확실할 경우 방어적 복사 생략 가능(단 문서화 필요)
- 메서드나 생성자의 매개변수로 넘기는 행위가 객체의 통제권을 이전하는 것을 뜻하는 경우 생략가능
    - 다만 클라이어트가 기존 가변객체를 수정하지 않는 것을 명확히 해야함

### 방어적 복사를 생략해도 되는 경우

- 클래스와 클라이언트가 상호 신뢰할 수 있을 때
- 불변식이 깨지더라도 그 영향이 오직 호출한 클라이언트로 국한될 때로 한정
    - ex) 래퍼 클래스
