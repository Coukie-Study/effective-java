# public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

## public 접근 제어자

---

```java
public class HelloWorld {
    public String hello;
    public String world;
}
```

- 위의 클래스는 직접 필드에 접근 가능하기 때문에 캡슐화의 이점을 제공하지 못한다.
    - 불변식을 보장할 수 없으며, 외부에서 필드에 접근할 때 부수 작업을 수행할 수도 없다.

## private 접근 제어자

---

```java
public class HelloWorld {
    private String hello;
    private String world;

    public String getHello() {
        return hello;
    }

    public String getWorld() {
        return world;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    public void setWorld(String world) {
        this.world = world;
    }
}
```

- 기존 클래스를 위와 같이 바꾼다면 접근자를 제공함으로써 클래스 내부 표현 방식을 언제든 바꿀 수 있는 유연성을 얻을 수 있다. (ex. 필드를 바꾸고 Getter에서 제공방식 수정)
- 다만 pakcage-private 클래스 혹은 private 중첩 클래스의 경우 데이터 필드를 직접 노출한다 해도 문제가 없다.
    - 클래스가 표현하려는 추상 개념만 올바르게 표현된다면 오히려 접근자 방식이 코드 상 깔끔하며, 패키지 외부 영향 고려 없이 수정 가능하다.
    - 중첩 클래스의 경우 수정범위는 더 좁아져 외부 클래스까지 제한할 수 있다.
- 자바 라이브러리 예제(필드 직접 노출)

[Source for java.awt.Dimension](http://developer.classpath.org/doc/java/awt/Dimension-source.html)

- 필드가 불변이라면 직접 노출할 때의 위험은 줄어들지만, 여전히 필드를 직접 읽기에 부수 작업이 불가능하고, 표현 방식을 바꿀 수 없게 된다. 단 불변식은 보장할 수 있다.

```java
public final class Time {
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;

    public final int hour;
    public final int minute;

    public Time(int hour, int minute) {
        if (hour < 0 || hour >= HOURS_PER_DAY) return new IllegalArgumentException("시간: " + hour);
        if (minute < 0 || minute >= MINUTES_PER_HOUR) {
            throw new InterruptedException("분: " + minute);
        }
        this.hour = hour;
        this.minute = minute;
    }
}
```