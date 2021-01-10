# 아이템 1. 생성자 대신 정적 팩터리 메서드를 고려하라.
## 정적 팩터리 메서드(static factory method)란?
생성자 대신 클래스의 인스턴스를 반환하는 정적 메서드
## 정적 팩터리 메서드의 장점
#### 1. 이름을 가질 수 있다.
정적 팩터리 메서드는 이름이 없는 생성자와 달리 메서드의 이름으 생성되는 인스턴스의 의미를 파악 하기 유리하다.
```java
//생성자를 이용하여 소수값을 갖는 인스턴스 생성
BigInteger bi1 = new BigInteger(10,1,new Random());
//정적 팩터리 메서드를 이용하여 소수값을 갖는 인스턴스 생성
BigInteger bi2 = BigInteger.probablePrime(10,new Random());
```

#### 2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.
같은 객체의 생성이 자주 요구되는 경우 동일한 객체(같은 메모리의 주소값을 갖는 객체)를 반환하여 메모리를 효과적으로 사용할 수 있.

```java
//Boolean 클래스 일부
public static final Boolean TRUE = new Boolean(true);
public static final Boolean FALSE = new Boolean(false);

public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }
```
Boolean 클래스의 정적 팩터리 메서드인 Boolean.valueOf 를 살펴보면<br> 
매개변수 boolean b에 따라 TRUE, FALSE 두가지 인스턴스만 리턴함을 알 수 있다.
#### 3. 반환 타입의 하위 타입 객체를 반환할 수 있다.
하위 타입 클래스를 클래스 내부에 구현하고 정적 팩터리 메서드를 통해 <br>하위 타입 클래스를 반환하여 간결하고 유연한 API를 만들 수 있다.<br>
(예제코드 ch1.Box 참고)
#### 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
정적 팩터리 메서드의 입력 변수에 따라 서로 다른 하위타입 클래스를 리턴할 수 있다.<br>
(예제코드 ch1.ParameterFactoryTest 참고)
#### 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.
이 장점은 JDBC와 같은 서비스 제공자 프레임워크를 만드는데 있어 도움이 된다.<br>
- 서비스 제공자 프레임워크의 핵심 컴포넌트
    - 서비스 인터페이스 : 구현체의 동작을 정의할 때 사용
    - 제공자 등록 API : 제공자가 구현체를 등록할 때 사용
    - 서비스 접근 API : 클라이언트가 서비스의 인스턴스를 얻을 때 사용

이는 각각 JDBC의 Connection, DriverManager.registerDriver, DriverManager.getConnection에 해당되고<br>
새로운 DBMS가 생겨나도 제공자 등록 API를 통해 등록하여 동일한 인터페이스로 사용이 가능하다.

## 정적 팩터리 메서드의 단점
#### 1. 상속을 하려면 public또는 protected 생성자가 필요하여 정적 팩터리 메소드만 제공하면 하위 클래스를 만들 수 없다.
예를 들어 컬렉션 프레임워크의 유틸리티 구현 클래스들 상속이 불가능하다.
#### 2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.
생성자와는 달리 정적 팩토리 메서드는 말그대로 메서드이기 때문에 일반 메서드와 구분이 힘들다.

## 정적 팩터리 메서드의 명명 방식
#### from
매개 변수를 하나 받아 해당 타입의 인스턴스 반환
```java
Date d = Date.from(instance)
```
#### of
여러 매변수를 받아 적합한 타입의 인스턴스를 반환
```java
Set<Rank> faceCards = EnumSets.of(JACK, QUEEN, KING);
```
#### valueOf 
from과 of의 더 자세한 버전
```java
BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);
```
#### instance 혹은 getInstance
매개변수로 명시한 인스턴스를 반환하지만 같은 인스터임은 보장하지 않늗다.
```java
StackWalker luke = StackWalker.getInstance(options);
```
#### create 혹은 newInstance
instance, getInstance와 같지만 매번 새로운 인스턴스를 생성해 반환함을 보장한다.
```java
Object newArray = Array.newInstance(classObject, arrayLen);
```
#### getType(Type : 반환 객체 타입)
getInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할때 쓴다.
```java
FileStore fs = Files.getFileStore(path);
```
#### newType(Type : 반환 객체 타입)
newInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할때 쓴다.
```java
BufferedReader br = Files.newBufferedReader(path);
```
#### Type
getType, newType의 간결한 버전
```java
List<Complaint> litany = Collections.list(legacyLitany);
```
