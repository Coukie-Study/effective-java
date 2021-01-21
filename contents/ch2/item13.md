# 아이템 13. clone 재정의는 주의해서 진행하라.
## clone 이란?
Object 클래스의 메서드로 필드값이 같은 인스턴스를 반환하는 메서드.<br>
```java
//Object 클래스 일부
protected native Object clone() throws CloneNotSupportedException;
```
## clone을 사용하려면 Cloneable 인터페이스를 구현하라.
보통 인터페이스를 구현한다는것은 인터페이스에서 정의한 기능을 제공한다는 의미이다.<br>
하지만 Cloneable 인터페이스는 아무런 메서드가 존재하지 않는 인터페이스다.<br>
그럼에도 clone 메서드를 사용하기위해 Cloneable을 구현해야 하는 이유는 Cloneable을 구현하지<br>
않은채 clone 메서드를 사용하면 CloneNotSupportedException이 발생하기 때문이다.<br>  
이는 Cloneable 인터페이스가 일반적인 인터페이스의 쓰임과는 달리 상위 클래스(Object)에 정의된<br>
protected 메서드(clone 메서드)의 동작 방식을 변경하기 위함으로 쓰였기 때문이다.

## Object 명세서에 적혀있는 clone 메서드의 일반 규약
- x.clone() != x 
- x.clone().getClass() == x.getClass()
- x.clone().equals(x)
- 관례상 반환된 객체와 원본 객체는 독립적이어야 한다.<br>
 이를 만족하려면 super.clone으로 얻은 객체의 필드중 하나 이상을 반환 전에 수정해야 할 수도 있다.
 
위 규약은 모두 강제성이 없고 일반적으로 지켰으면 하는 규약이다.

## clone 메서드 재정의 방법
#### 1. 클래스의 모든 필드가 기본 타입이거나 불변 객체인 경우
위 마지막 규약을 보면 반환된 객체와 원본 객체는 독립적이어야 한다.<br>
즉 복제한 인스턴스와 복제된 인스턴스의 참조값은 물론이고, 각각의 필드가 참조하는 값 또한
불변이 아니라면 달라야한다. 그러므로 모든 필드가 기본타입이거나 불변 객체라면 이 규약을 Object 클래스에서 
구현되어있는 clone이 이미 보장하기 때문에 굳이 손 볼 필요가 없다.

```java
class Human implements Cloneable {
  int age;
  String name;

  @Override
  // 상위 클래스가 반환하는 타입의 하위클래스를 반환할 수 있다.
  public Human clone() {
    try {
      return (Human) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
```
#### 2. 클래스에 가변 객체 필드가 존재하는 경우 
````java
class A implements Cloneable{
  int number;
  Object[] list;
  public A(){
    list = new Object[10];
  }
  @Override
  public A clone() throws CloneNotSupportedException{
    A result = (A)super.clone();
    result.list = list.clone();
    return result;
  }
}
````
위 클래스의 clone을 super.clone()으로만 구현할 경우 복사된 인스턴스와 기존 인스턴스의<br>
list 필드의 참조값이 같아지므로 list는 list.clone()을 이용하여 따로 복사한다.<br><br>
*배열의 경우 clone()의 리턴값이 기존의 필드 타입과 같기 때문에 형변환을 해 줄 필요가 없다.

#### 3. 복잡한 가변 상태를 갖는 클래스용 재귀적 clone 메서드
ch2.item13.HashTable 참고

#### 4. 복잡한 가변 상태를 갖는 클래스의 다른 clone 메서드 정의 방법
1. super.clone()을 호출하여 얻은 객체의 모든 필드를 초기 상태로 설정
2. 원본 객체의 상태를 다시 생성하는 고수준 메서드들을 호출
* clone 메서드 안에선 재정의한 메서드를 사용하면 안되기 때문에 위 방법에서 사용하는 메서드를 private 혹은 final로 선언헤야 한다.

## clone 메서드 주의사항

- public인 clone 메서드에서는 사용의 편리성을 위해 throws 절을 없애라.
- 상속용 클래스는 Cloneable을 구현해선 안된다.
    1. Object 처럼 clone 메서드를 구현해 protected로 두고 CloneNotSupportedException을 던질 수 있다 선언한다.
    2. ````java
       @Override
       protected final Object clone() throws CloneNotSupportedException{
           throw new CloneNotSupportedException();
       }
       ````
       위처럼 clone을 퇴화 시켜논다.
- 스레드 안전클래스를 작성할 때는 clone 메서드 역시 동기화 해줘야 한다.

## 요약
1. clone 메서드를 사용하려면 Cloneable을 구현하고 clone을 재정의 해야한다.
2. clone 메서드의 접근 제한자는 public으로, 반환 타입은 클래스 자신으로 변경한다.
3. 가장먼저 super.clone을 호출한 후 필요한 필드를 적절히 수정한다.

## clone을 사용하는것 보다 복사 생성자와 복사 팩터리라는 더 나은 방식을 사용하라.
복사 생성자
````java
public Yum(Yum yum){ ... };
````

복사 팩터리
````java
public static Yum newInstance(Yum yum){ ... };
````  

#### clone 메서드 보다 나은 이유
1. 생성자를 쓰지 않는 방식은 위험한 객체 생성 메커니즘이다.
2. 엉성하게 문서화된 규약에 기대지 않는다.
3. 정상적인 final 필드 용법과도 충돌하지 않는다.
4. 불필요한 검사 예외를 던지지 않는다.
5. 형변환도 필요하지 않다.
6. 해당 클래스가 구현한 인터페이스 타입의 인스턴스를 인수로 받을 수 있다. <br>
ex) HashSet 객체를 TreeSet 타입으로 복제할 수 있다.
