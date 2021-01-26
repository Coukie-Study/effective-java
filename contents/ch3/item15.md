# 클래스와 멤버의 접근 권한을 최소화하라

## 정보 은닉

- 내부의 구현 정보를 숨기고 구현과 API를 깔끔히 분리하여 오직 API를 통해서만 다른 컴포넌트와 소통. 내부 동작 방식에는 관심을 두지 않게 됨

    ```java
    public class HelloWorld { 
        public String hello;
        public String world;
    }
    
    public class HelloWorld { 
        private String hello;
        private String world;
        
        // method
    }
    ```

## 정보 은닉의 장점

- 시스템 개발 속도를 높인다. 여러 컴포넌트를 병렬로 개발할 수 있기 때문
- 시스템 관리 비용을 낮춘다. 각 컴포넌트를 더 빨리 파악 가능하여 디버깅 시 유리하고 다른 컴포넌트로 교체하는 부담도 적어진다.
- 성능 자체를 높여주진 않지만, 성능 최적화에 도움을 준다. 완성된 시스템을 프로파일링해 최적화할 컴포넌트를 정한 다음, 다른 컴포넌트에 영향을 주지 않고 해당 컴포넌트만 최적화할 수 있기 때문이다.
- 소프트웨어 재사용성을 높인다. 외부에 의존적이지 않은 컴포넌트는 다른 환경에서도 유용하게 쓰일 수 있다.
- 큰 시스템을 제작하는 난이도를 낯춰준다. 완성되지 않은 상태에서도 개별 컴포넌트의 동작을 검증할 수 있기 때문.

## 자바 정보 은닉

- 자바는 클래스, 인터페이스, 멤버의 접근성을 명시
- 각 요소는 선언된 위치와 접근 제한자를 통해 접근성이 정해짐

> public : 모든 곳에서 사용가능
protected : 같은 패키지 또는 자식 클래스
package-protected(default) : 같은 패키지에서 사용가능
private : 클래스 내부

### 접근 제한자를 사용하는 원칙

- 기본원칙 : **모든 클래스와 멤버의 접근성을 가능한 좁혀야 한다.**

### Class, Interface

- (Top level) 클래스와 인터페이스에서 사용할 수 있는 접근 제한자는 public, package-private
- public으로 선언하면 외부에서 사용할 수 있는 공개 API(이 클래스가 제공하는 기능)가 되며 외부와의 호환을 위한 관리가 필요
- package-private으로 설정하면 내부 구현이 되어 수정이 용이하다.
- 한 클래스에서만 사용하는 class나 interface는 private static class로 중첩시켜보자. 이렇게 구현할 경우 B 클래스는 A에서만 접근 가능하다.
- [Static vs Non static](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html)

    ```java
    public class A { 
        private B b;
        // ...
        private static B {
            // ...}
        }
    }
    ```

### Member

- 필드, 메서드, 중첩 클래스, 중첩 인터페이스
- 위에서 설명한 private, package-private, protected, public
- 클래스의 공개 API를 설계 후, 나머지는 private로 만들자. 그 후 같은 패키지의 다른 클래스가 접근해야 하는 경우의 멤버만 package-private로 변경
- 만약 권한을 공개적인 방향으로 바꾸는 일이 많아진다면 컴포넌트를 분해하는 것을 고려
- private이나 package-private 멤버는 구현에 해당하므로 공개 API에 영향을 주진 않지만, Serializable을 구현한 경우 의도치 않게 공개 API가 될 수 있기에 조심해야 한다.

    ```java
    public class A implements Serializable {
        private String info;
        private String test;
        // private int age; 만약 Serializable을 구현한 클래스를 배포 후, 새로운 내부 필드가 추가된다면?
    }
    ```

- 멤버 접근성을 protected로 바꾼다면 접근할수 있는 대상 범위가 엄청나게 늘어나게 된다.  public class의 protected 멤버또한 공개 API이므로 지원 대상이며, 또한 상속을 고려해 내부 동작 방식 또한 API 공개해야 하기 때문에 가능하면 protected 멤버의 수는 적을수록 좋다.
- 다만 상위 클래스의 메서드를 재정의할 때는 접근 수준은 상위 클래스보다 좁게 설정할 수 없다.(리스코프 치환 원칙)

    ```java
    // Point.java
    public class Point { 
        public void hello() {
            //...
        }
    }
    
    // ColorPoint.java
    public class ColorPoint extends Point { 
        @Override 
        private void hello() { // compile error
            // ...
        }
    }
    ```

## 주의사항

- 테스트 목적으로 접근 제한자의 범위를 넓히는 것은 package-private까지는 허용할 수 있지만 그이상은 안된다.
- public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다.
    - 필드를 제한한 방법을 잃게 되며, 불변식을 보장할 수 없다.
    - 필드가 수정될 때 다른 작업(ex. 락 획득)을 할 수 없게 되어 일반적으로 스레드 안전하지 않다. (직접 필드 값을 수정하기 때문에)
- 다만 public static final로 필드를 공개하는 경우가 있는데, **상수**를 사용하는 경우이다.
    - 이 필드는 기본 타입이나 불변 객체를 참조해 일종의 값을 대표하는 역할을 수행한다.
    - 가변 객체를 참조한다면 객체 자체가 수정될 수있다는 단점으로 인해 예기치 못한 결과가 나올 수 있다.
    - 또한 배열 필드를 사용해도 배열 값을 바꿀 수 있기에 사용해서는 안된다.
        - 또한 배열 필드는 필드를 반환하는 공개 메서드를 제공해서도 안된다.
        - 불변 리스트를 추가하거나, 방어적 복사를 통해 제공하는 것을 고려해보자.

## 모듈

- 자바9에서 추가된 개념으로 접근 수준을 추가
- 모듈은 일종의 패키지들의 묶음
- 모듈은 자신이 속하는 패키지 중 공개할 것을 선언(관례적으로 [module-info.java](http://module-info.java) 사용)
    - 공개하지 않는 public 멤버는 모듈 내부에서만 사용
- 모듈을 적용하기 위해서는 적용해야 할 것이 많기에 추이를 지켜보고 당분간 사용하지 마라
- [참고](https://www.baeldung.com/java-9-modularity)
