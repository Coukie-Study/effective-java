# 아이템 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라.
## 싱글턴(Singleton)이란 ?
인스턴스를 오직 하나만 생성할 수 있는 클래
## 클래스를 싱글턴으로 만드는 방법
### 1. public static final 필드 방식
```java
public class Elvis{
    public static final Elvis INSTANCE = new Elvis();
    private Elvis(){}
    public void leaveTehBuilding(){
        System.out.println("Bye building!");
    }
}
```
#### 장점
    1. 해당 클래스가 싱글턴임이 API에 명백히 드러난다.
    2. 간결하다. 
### 2. 정적 팩터리 방식의 싱글턴
```java
public class Elvis{
    private static final Elvis INSTANCE = new Elvis();
    private Elvis(){}
    public static Elvis getInstance(){
        return INSTANCE;
    }    
    public void leaveTehBuilding(){
        System.out.println("Bye building!");
    }
}
```
#### 장점
    1. API를 바꾸지 않고 getInstance 내부 구현만 바꾼다면 싱글턴이 아니게 변경할 수 있다. 
    2. 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다.
    3. 정적 팩터리의 메서드 참조를 공급자로 사용할 수 있다.
    
    위의 장점을 사용하지 않을 거면 public field 방식이 더 좋다.
    
#### 위 두가지 방법의 단점
- 리플렉션 API를 사용해 private 생성자를 호출할 수 있다.

    리플렉션 API인 AccesibleObject.setAccessible을 사용하면 private 생성자를 호출할 수 있다.<br>
    예방하려면 생성자에 두 번재 객체가생성되려 할 때 예외를 던져야 한다.
- 단순히 직렬화가 불가능 하다.

    단순히 Serializable을 구현한다고 선언하는 것만으로는 부족하다. 모든 인스턴스 필드를 일시적(transient)로 선언하고<br>
    readResolve 메서드를 제공해야 한다.
    ```java
    private Object readResolve(){
      // '진짜' Elvis를 반환하고, 가짜 Elvis는 가비지 컬렉터에 맡긴다.
      return INSTANCE;
    }
    ```
### 3. 열거 타입 방식의 싱글턴 - 바람직한 방법
```java
public enum Elvis{
    INSTANCE;
 
    public void leaveTehBuilding(){
        System.out.println("Bye building!");
    }
}
```
#### 장점
    1. 가장 간결하다.
    2. 추가 노력 없이 직렬화가 가능하다.
    3. 제2의 인스턴스가 생기는 일을 완벽히 막아준다.
    4. 대부분 상황에서는 가장 좋은 방법이다.
    
    다만 만들려는 싱글턴이 Enum 외의 클래스를 사속해야 한다면 이 방법은 사용 할 수 없다.