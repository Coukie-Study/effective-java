# 인스턴스 수를 통제해야 한다면 readResolve 보다는 열거 타입을 사용하라.

### readResolve 메서드
readResolve 기능을 이용하면 readObject가 만들어낸 인스턴스를 다른 것으로 대체할 수 있다.
<br> 역직렬화한 객체의 클래스가 readResolve 메서드를 적절히 정의해뒀다면, 역직렬화 후 새로 생성괸 객체를 인수로
<br> readResolve메서드가 호출되고, 이 메서드가 반환한 객체 참조가 새로 생성된 객체를 대신해 반환한다.


### readResolve 사용 예
````java
// 싱글턴 패턴
public class Elvis{
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }
    
    public void leaveTheBuilding() { ... }
}

// readResolve
private Object readReslove(){
// 진짜 Elvis(INSTANCE)를 반환하고 가짜 Elvis는 가비지 컬렉터에 맡긴다.
    return INSTANCE;
}
````
### 유의사항
- 이미 만들어진 객체를 반환하므로 어떠한 필드도 직렬화할 필요가 없기에 모든 필드를 transient로 선언해야한다.
- transient 가 아닌 필드가 있다면 readResolve 메서드가 실행되기 전 역직렬화될때 잘 조작된 스트림을 사용하면 인스턴의 참조를 훔쳐올 수 있다.
- readResolve 메서드의 접근성은 매우 중요하다.
    - final 클래스에서라면 private 으로 선언해야 한다.
    - protected나 public이면서 하위 클래스에서 readResolve 메서드를 재정의 하지 않았다면 하위클래스의 인ㄴ스턴스를 역직렬화를 하면
    <br> 상위클래스의 인스턴스를 생성하여 ClassCastException을 일으킬 수 있다.
    
### 결론
싱글턴은 원소가 하나인 Enum과 동일하다. Enum은 직렬화에 대한 구현이 이미 되어있으므로 Enum을 사용하는것이 좋다.
