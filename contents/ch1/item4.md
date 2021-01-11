# 아이템 4. 인스턴스화를 막으려거든 private 생성자를 사용하라.
## 인스턴스화를 막는 클래스
정적 메서드와 정적 필드만을 담은 클래스는 생성하는 의미가 없기 때문에 <br>
불필요한 인스턴스화를 막는다.

- java.lang.Math
- java.util.Arrays

위와 같이 기본 타입 값이나 배열 관련 메서드들을 모아놓을 수 있다.
- java.util.Collections

처럼 특정 인터페이스를 구현하는 객체를 생성해주는 정적 메서드(혹은 팩터리)를 모아 놓을 수 있다.<br>
또는 final 클래스와 관련한 메서드들을 모아놓을 때도 사용한다.

## 인스턴스화를 막는 방법
#### 추상 클래스로 구현으로는 인스턴스화를 막을 수 없다.
단순히 추상 클래스로 만드는 것으로는 하위클래스로 인스턴스화가 가능해 인스턴스화를 막을 수 없다.

#### private 생성자를 추가
```java
public class UtilityClass{
    private UtilityClass(){
        throw new AssertionError();
    }
}
```
위와 같이 private 생성자를 추가하면 외부에서 생성자로 인스턴스화가 불가능하다.<br>
꼭 AssertionError를 던질 필요는 없지만 클래스 안에서 실수로 생성자를 호출하는 것을 방지해준다.