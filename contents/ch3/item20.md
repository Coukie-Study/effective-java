# 20. 추상 클래스보다는 인터페이스를 우선하라

자바가 제공하는 다중 구현방식은 두가지가 있다. 바로 인터페이스와 추상클래스이다.

- 추상 클래스의 경우, 추상 클래스에서 정의한 메서드를 구현하는 클래스는 반드시 추상 클래스의 하위 클래스가 되어야 같은 타입으로 취급한다.
- 인터페이스의 경우, 인터페이스에서 정의한 메서드를 모두 정의한 클래스라면 다른 어떤 클래스를 상속했든 상관없이 같은 타입으로 취급한다.



### 기존 클래스에도 손쉽게 새로운 인터페이스를 구현해 넣을 수 있다.

- 현재 시스템에 운영되고 있는 어떤 클래스에 새로운 인터페이스를 구현하기 위해서는 큰 복잡함 없이 그저 기존 클래스에 implements를 사용하여 인터페이스를 정의해주고 인터페이스에서 정의하는 메소드만 구현하면 끝이다.
- 하지만 추상 클래스는 계층 구조이기 때문에 기존 클래스의 성격을 잘 파악해야 한다. 만약 클래스가 두개가 있고 이 클래스의 추상 클래스를 선언하려고 할 시에는 두 클래스가 같은 조상을 바라보는 연관된 추상 클래스를 선언해야 한다.



### 인터페이스는 믹스인 정의에 안성맞춤이다.

믹스인이란 어떤 클래스의 주 기능에 추가적인 기능을 혼합한 것이다.

가장 적절한 예는 Comparable를 사용한 예이다.

```java
public class Employee implements Comparable<Employee>{
  private int id;

  public Employee(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public int printId() {
    return this.id;
  }

  @Override
  public int compareTo(Employee o) {
    if(o.getId() < this.id) {
      return -1;
    } else if(o.getId() == this.id) {
      return 0;
    } else {
      return 1;
    }
  }
}
```

이런식으로 Comparable을 구현한 클래스는 같은 클래스 인스턴스끼리 순서를 정할 수 있는 추가적인 기능을 혼합한 것이다.



### 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다.

현실 세계에는 부모와 자식처럼 계층구조가 잘 이루어진 개념이 있는 반면, 가수와 작곡가 그리고 가수겸 작곡가와 같은 계층적으로 표현하기 어려운 개념도 존재한다 이런 개념은 인터페이스에서 만들기 편하다.

```java
public interface Singer{
  public void sing();
}

public interface SongWriter{
  public void compose();
}

public interface SingerSongWriter extends Singer, SongWriter{
  public void actSensitive();
}
```

이 처럼 인터페이스의 경우 두 가지 이상 확장이 가능하므로 Singer와 SongWriter를 모두 확장한 SingerSongWriter인터페이스를 구현할 수가 있는 것이다.

하지만 이 부분을 추상 클래스로 구현했을 때는

```java
public abstract class Singer {
    abstract void sing();
}
public abstract class SongWriter {
    abstract void compose();
}

public abstract class SingerSongWriter {
    abstract void actSensitive();
    abstract void sing();
    abstract void compose();
}
```

이 처럼 추상 클래스를 두 개 이상의 클래스를 상속할 수 없기 때문에 SingerSongWriter라는 추상 클래스를 만들어 추상 메소드를 추가할 수 밖에 없다.



### 추상 골격 구현 클래스

인터페이스에 디폴트 메소드의 기능을 제공해주면서 개발자들이 중복되는 메소드의 구현을 하는 수고를 덜어주었다.

하지만 이폴트 메소드의 경우 여러 단점이 존재하기 때문에 추상 골격 구현 클래스를 제공함으로써 인텊페이스와 추상 클래스의 장점을 모두 가져갈 수 있다.

디폴트 메소드 단점

1. Object 메소드인 equals와 hashcode를 디폴트 메소드로 제공해서는 안된다.
2. 인터페이스 인스턴스 필드를 가질 수 없고 public이 아닌 정적 메소드를 가질 수 없다.

```java
//추상 골격 구현 클래스 사용 하지 않는 버전.
public interface Character {
  public void move();
  public void seat();
  public void attack();
}

public class Thief implements Character{
  @Override
  public void move() {
    System.out.println("걷다");
  }

  @Override
  public void seat() {
    System.out.println("앉다");
  }

  @Override
  public void attack() {
    System.out.println("표창을 던진다");
  }    
}

public class Wizard implements Character{
  @Override
  public void move() {
    System.out.println("걷다");
  }

  @Override
  public void seat() {
    System.out.println("앉다");
  }

  @Override
  public void attack() {
    System.out.println("마법봉을 휘두르다");
  }
}

public static void main(String[] args) {
  Thief thief = new Thief();
  Wizard wizard = new Wizard();
  thief.process();
  wizard.process();
}
```

위 소스에서 보는 바와 같이 attack() 메소드를 제외하고는 모두 중복되는것을 볼 수 있다. 이런 중복된 부분을 추상 골격 구현 클래스를 이용하여 정의하는 것이다.

```java
//추상 골격 구현 클래스 사용하는 버전
public abstract class AbstractCharacter implements Character{
  @Override
  public void move() {
    System.out.println("걷다");
  }

  @Override
  public void seat() {
    System.out.println("앉다");
  }
}

public class Thief extends AbstractCharacter implements Character{
    @Override
    public void attack() {
        System.out.println("표창을 던진다");
    }
}

public class Wizard extends AbstractCharacter implements Character{
    @Override
    public void attack() {
        System.out.println("마법봉을 휘두르다");
    }
}
```

이 처럼 디폴트 메소드를 사용하지 않고 추상 골격 구현 클래스를 구현하여 중복을 없앨 수 있다.



정리

- 일반적으로 다중 구현용 타입으로는 인터페이스가 가장 적합하다.
- 복잡한 인터페이스라면 구현하는 수고를 덜어주는 골격 구현을 함께 제공하는 방법을 고려하자.



























