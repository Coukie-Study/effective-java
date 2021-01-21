# 아이템6. 불필요한 객체 생성을 피하라

### 똑같은 기능의 객체를 매번 생성하기보다는 객체 하나를 재사용하는 편이 좋을 때가 많다.
```java
// bad
String s = new String("hi");

// good
String s = "hi";
```
String 객체를 새로 생성하기보다는 String pool로 문자열을 관리하자.
[링크1](https://github.com/Coukie-Study/effective-java/issues/2#issuecomment-758403591)

String 생성 과정에 대한 보다 자세한 설명
[링크2](https://codingdog.tistory.com/entry/java-string-intern-%EB%A9%94%EC%84%9C%EB%93%9C-pool%EC%9D%B4-%EB%90%9C%EB%8B%A4%EB%8A%94-%EA%B2%83%EB%A7%8C-%EA%B8%B0%EC%96%B5%ED%95%A9%EC%8B%9C%EB%8B%A4)

String literals
[링크3](https://docs.oracle.com/javase/specs/jls/se15/html/jls-3.html#jls-3.10.5)

### 정적 팩터리 메서드(item1)를 사용해 불필요한 객체 생성을 피할 수 있다.
```java
// good
Boolean.valueOf(String)
```
생성자는 호출할 때마다 새로운 객체를 만들지만, 팩터리 메서드는 그렇지 않다.

### 생성 비용이 비싼 객체는 반복해서 사용하자.
정규표현식용 Pattern은 입력받은 정규표현식에 해당하는 유한 상태 머신을 만들기 때문에 인스턴스 생성 비용이 높다.
따라서 필요한 Pattern 인스턴스를 클래스 초기화 과정에서 직접 생성해 캐싱하고, 나중에 이 인스턴스를 재활용하는 방식으로 사용하자.

```java
static boolean isRomanNumeralSlow(String s) {
    return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
    + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```
위 코드는 로마 숫자인지 확인하는 가장 쉬운 방법이겠지만 문제는 String.matches를 사용하는데 있다.
```java
// String.class
public boolean matches(String regex) {
    return Pattern.matches(regex, this);
}
    
// Pattern.class
public static boolean matches(String regex, CharSequence input) {
    Pattern p = compile(regex);
    Matcher m = p.matcher(input);
    return m.matches();
}

public static Pattern compile(String regex) {
    return new Pattern(regex, 0);
}
```
위 코드를 보면 알 수 있지만 내부적으로 새로운 Pattern 인스턴스를 생성하고 곧바로 버려지는 것을 알 수 있다.
```java
public class RomanNumerals {
    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeralFast(String s) {
        return ROMAN.matcher(s).matches();
    }
}
```
위와 같이 Pattern 객체를 재사용함으로써 성능뿐만 아니라 코드도 더 명확해지는 장점이 존재한다.

다만 isRomanNumeralFast 메서드를 호출하지 않는다면 위의 ROMAN 필드는 쓸데없이 초기화된 꼴이다. 
지연 초기화를 적용할 수 있겠지만 성능이 크게 개선되지 않는 경우가 많다.(item 67)

### 어댑터 사용 시 불필요한 객체 생성 방지
객체가 불변이라면 재사용해도 안전한게 당연하지만, 때때로 불변 객체가 아니더라도 재사용할 수 있는 경우가 있다. 그 대표적인 예시가 어댑터이다.
어댑터는 실제 작업을 뒷단 객체에 위임하고, 자신은 제2의 인터페이스 역할을 해주는 객체이다. 
즉 이 어댑터는 뒷단 객체 외에는 관리할 것이 없으므로 뒷단 객체 당 어댑터 하나씩만 만들어지면 충분하다.

```java
// HashMap.class
public Set<K> keySet() {
    Set<K> ks = this.keySet;
    if (ks == null) {
        ks = new HashMap.KeySet();
        this.keySet = (Set)ks;
    }

    return (Set)ks;
}
```
Map의 KeysSet을 호출할 때마다 새로운 객체를 생성할 수도 있겠지만, 위에서 볼 수 있듯 자신의 필드값을 재사용한다.
this.keySet이 변하면 이 KeySet을 활용한 다른 객체싱도 상태 변경이 있지만 그렇다고 해서 새로운 객체를 생성할 필요도 없고 이득도 없는 경우인 것을 알 수 있다.

다만 위의 견해에 동의하지 않는다면 방어적 복사(item 50)을 방식을 생각해 볼 수도 있을 것이다.

### 불필요한 오토 박싱 방지
>Auto Boxing : 프로그래머가 기본 타입과 박싱된 기본 타입을 섞어 쓸 때 자동으로 상호 변환(primitive <-> reference) 해주는 기술

박싱된 값과 기본 타입의 값의 비교는 의미상으로 별다를 것 없지만 성능에서는 그렇지 않다.
```java
private static long sum() {
    Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++)
        sum += i;
    return sum;
}
```
위에서 단 한줄의 `Long sum = 0L;`가 변수 i의 오토 박싱을 유발하고 성능상 문제를 유발한다.
따라서 박싱된 기본 타입보다는 기본 타입을 사용, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자.

### 주의할 점
다만 "객체 생성은 비싸니 피해야 한다."로 오해하는 것은 잘못된 생각이다. 요즘의 컴퓨터는 그렇게 느리지 않다.(작은 객체를 생성, 회수하는 일은 크게 부담이 되지 않는다.)
오히려 프로그램의 명확성, 간결성, 기능을 위해서 객체를 추가로 생성하는 것이 일반적으로 좋은 일이다.

자체 객체 풀을 만들어 재사용하는 경우도 있겠지만, 생성 비용이 비싼 객체가 아닌 경우 오히려 코드 가독성을 떨어뜨리고, 메모리 사용량을 늘려 성능을 떨어뜨리는 경우도 발생한다.
따라서 JVM을 믿고 최적화를 믿자.

이 아이템은 추후 item50. 방어적 복사와 비교해서 읽어보면 좋을 것이다.