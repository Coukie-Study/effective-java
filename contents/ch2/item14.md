# item 14. Comparable을 구현할지 고려하라.

## Comparable 이란?
````java
public interface Comaparable<T>{
    int compareTo(T t);
}
````
compareTo 라는 유일한 메서드를 통해 인스턴스들 간 순서 비교를 가능하게 하는 인터페이스.<br>
순서가 명확한 값 클래스(알파벳, 숫자 등)을 작성하면 반드시 구현해야 한다.

## compare 메서드의 일반 규약
- 객체가 비교하는 객체보다 작으면 음의 수, 같으면 0, 크면 양의 정수를 반환한다.
- 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다.
- sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) (따라서 x.compareTo(y)는 y.compareTo(x)가 예외를 던질때에 한해 예외를 던져야 한다.)
- x.compareTo(y) > 0 && y.compareTo(z) > 0 이면 x.compareTo(z) > 0 이다.
- x.compareTo(y) == 0 이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z))다.
- (x.compareTo(y) == 0) == (x.equals(y)) 필수는 아니지만 꼭 지키는게 좋다.

#### 기존 클래스를 확장한 구체 클래스의 경우
equals와 마찬가지로 구체 클래스에 새로운 값 컴포넌트를 추가했다면 기존의 compareTo 규약을 지킬 방법이 없다.<br>
그러므로 확장하는대신 독립된 클래스를 만들고, 원래 클래스의 인스턴스를 가리키는 필두를 두는것이 낫다.

#### x.compareTo(y) == 0 이면 x.equals(y) == true를 왠만하면 지켜라
위 식이 성립하지 않더라도 클래스는 여전히 동작하지만, 이 클래스의 객체를 정렬된 컬렉에 넣으면<br>
해당 컬렉션이 구현한 인터페이스(Collection, Set, 혹은 Map)에 정의된 동작과 엇박자를 낼 것이다.<br>

```java
public class tmp2 {
  public static void main(String[] args) {
    BigDecimal bd1 = new BigDecimal("1.0");
    BigDecimal bd2 = new BigDecimal("1.00");
    HashSet<BigDecimal> hs = new HashSet<>();
    TreeSet<BigDecimal> ts = new TreeSet<>();
    hs.add(bd1);
    hs.add(bd2);
    ts.add(bd1);
    ts.add(bd2);
    //결과 : 2
    System.out.println(hs.size());
    //결과 : 1
    System.out.println(ts.size());
  }
}
```

## compareTo 메서드 작성 요령
- 기본적으로 equals 메서드와 비슷하지만 Comparable은 타입을 인수로 받는 제네릭 인터페이스 이므로<br>
compareTo 메서드의 인수 타입이 컴파일타임에 정해지므로 인수타입을 확인하거나 형변환 할 필요가 없다.<br>
- 인수 타입이 잘못 되면 컴파일 자체가 안되고 null을 인수로 호출하면 인수의 멤버에 점근하는 순NullPointerException이 던져질 것이다.
- 객체 참조 필드를 비교하려면 그 필드의 compareTo 메서드를 재귀적으로 호출한다.
- Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 한다면 비교자(Comparator)를 대신 사용한다.
```java
public final class CaseInsensitiveString implements Comparable<CaseInsensitiveString>{
  String s;
  public int compareTo(CaseInsensitiveString cis){
    return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
  }
}
```
- 숫자형 필드를 비교할땐 관계연산자 <, > 를 이용하기보단 박싱된 기본 타입 클래스의 compare 메서드를 이용하라.

#### 클래스의 핵심 필드(비교해야 하는 필드)가 여러 개라면 핵심적인 필드부터 비교해 나가자.
```java
public int compareTo(PhoneNumber pn){
    int result = Short.compare(areaCode, pn.areaCode);   // 가장 중요한 필드
    if(result == 0){
        result = Short.compare(prefix, pn.prefix);       // 두 번째로 중요한 필드
        if(result == 0){
            result = Short.compare(lineNum, pn.lineNum); // 세 번째로 중요한 필드   
        }
    }   
}
```
#### 비교자 생성 메서드를 활용한 비교자
```java
private static final Comparator<PhoneNumber> COMPARATOR =
        comparingInt((PhoneNumber pn) -> pn.areaCode)
            .thenComparingInt(pn -> pn.prefix)
            .thenComparingInt(pn -> pn.lineNum);

public int compareTo(PhoneNumber pn){
    returrn COMPARATOR.compare(this, pn);
}
```
위 방식을 사용하면 약간의 성능 저하가 뒤따를 수 있다.

#### 객체 참조용 비교자 생성 메서드도 있다.
객체 참조 필드를 통해 비교하기 위한 객체 참조용 비교자 생성 메서드 또한 Comparator에 존재한다.<br>
(ch2.item14.PersonTest 참고)

#### '값의 차'를 기준으로 비교하는것을 주의하라
```java
static Comparator<Object> hashCodeOrder = new Comparator<>(){
    public int compare(Object o1, Object o2){
        return o1.hashCode() - o2.hashCode();
    }
}
```
위의 경우 정수 오버플로우를 일으켜 추이성을 위배할 수 있다.<br>
실수의 경우 부동소수점 계산 방식에 따른 오류를 낼 수 있다.

#### 대체 코드
정적 compare 메서드를 활용한 비교자
```java
static Comparator<Object> hashCodeOrder = new Comparator<>(){
    public int compare(Object o1, Object o2){
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
}
```
비교자 생성 메서드를 활용한 비교자
```java
static Comparator<Object> hashCodeOrder = 
        Comparator.comparingInt(o -> o.hashCode());
    }
}
```
