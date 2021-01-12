# 아이템5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라.
클래스가 하나 이상의 자원에 의존하고 사용하는 자원에 따라 동작이 달라지는 클래스에는 
정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.<br><br>
사전에 의존하는 맞춤법 검사기 클래스를 예로 들어보자.
#### 정적 유틸리티, 싱글턴은 적합하지 않다.
```java
//정적 유틸리티 사용
public class SpellChecker{
    private static final Lexicon dictionary = ...;
    
    private SpellChecker() {} //객체 생성 방지
    
    public static boolean isValid(String word) {...}
    public static List<String> suggestions(String typo) {...}
}
```
```java
//싱글턴 사용
public class SpellChecker{
    private static final Lexicon dictionary = ...;

    private SpellChecker(...) {} //객체 생성 방지
    public static SpellChecker Instance = new SpellChecker(...);

    public boolean isValid(String word) {...}
    public List<String> suggestions(String typo) {...}
}
```
#### 단점
- 오직 한가지의 의존 자원만 이용 가능하다.(유연성이 떨어진다)
- 테스트 용이성이 떨어진.
- 단순히 final 한정자를 제거하여 다른 사전으로 교체하는 메서드를 추가할 경우 자원을 공유하는 
위 두가지 방식에 있어 어색하고 오류를 내기 쉬우며 멀티 스레드 환경에서는 쓸 수 없다.


#### 의존 객체 주입을 사용하자
```java 
public class SpellChecker{
    private static final Lexicon dictionary = ...;

    public SpellChecker(Lexicon dictionary){
        this.dictionary = Objects.requireNonNull(dictionary);
    }    
    
    public boolean isValid(String word) {...}
    public List<String> suggestions(String typo) {...}
}
```
#### 장점
- 객체를 생성할때 다양한 자원을 넘겨줄 수 있다.
- 테스트 용이성이 좋다.
- 자원의 불변을 보장하여 여러 클라이언트가 안심하고 공유할 수 있다.
- 생성자, 정적 팩터리, 빌더 모두에 똑같이 응용할 수 있다. 
- 자원과 의존관계가 많아도 문제없이 작동한다. 다만 너무 많을 경우 코드를 어지럽게 만들 수 있다.