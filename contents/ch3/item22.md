# 아이템 22. 인터페이스는 타입을 정의하는 용도로만 사용하라.
### 인터페이스의 역할
- 인터페이스는 자신을 구현한 클래스의 인스턴스를 참조할 수 있는 타입 역할을 한다.
- 클래스가 인터페이스를 구현한다는 것은 자신의 인스턴스로 무엇을 할 수 있을지를 클라이언트에게 얘기해 주는것이다.
- 인터페이스는 오직 이 용도로 사용되어야 한다.

### 상수 인터페이스
상수 인터페이스란 메서드 없이, 상수를 뜻하는 static final 필드로만 가득 찬 인터페이스를 말한다.
````java
//상수 인터페이스
public interface PhysicalConstants{
    static final double AVOGADROS_NUMBER = 6.022_140_857e3;
    static final double BOLTZMANN_CONST  = 1.380_648_52e023;
    static final double ELECTRON_MASS    = 9.109_383_56e-31;
}
````
이는 인터페이스를 본래의 용도와는 다르게 잘못 사용한 예이다.

#### 상수 인터페이스의 문제점
- 클래스 내부에서 사용하는 상수는 내부구현에 해당되므로 상수 인터페이스를 구현하는것은 내부구현을 노출하는 것이다.
- 상수 인터페이스를 구현하는것은 상수 사용 외에 의미가 없으므로 사용자에게 혼란을 준다.
- 클라이언트 코드가 내부 구현에 해당하는 이 상수들에게 종속되어 이 상수들을 더 쓰지 않게 되더라도 바이너리 호환성을 위해 여전히 상수 인터페이스를 구현해야 한다.
- final이 아닌 클래스가 상수 인터페이스를 구현하면 그 클래스의 하위 클래스의 이름공간이 오염된다.

#### 상수를 공개할 목적의 클래스 구현 방법
##### 1. 클래스나 인터페이스에 강하게 연관된 상수면 그 클래스나 인터페이스 자체에 추가한다.
````java
public final class Integer extends Number implements Comparable<Integer> {
    @Native public static final int   MIN_VALUE = 0x80000000;
    
    /*
       ...
    */
}
````
##### 2. 열거 타입으로 나타내기 적합한 타입이면 열거 타입으로 만들어 공개한다.
##### 3. 인스턴스화할 수 없는 유틸리티 클래스에 담아 공개한다.
````java
package constantutilityclass;

public class PhysicalConstants{
    private PhysicalConstants() {} // 인스턴스화 방지
    public static final double AVOGADROS_NUMBER = 6.022_140_857e3;
    public static final double BOLTZMANN_CONST  = 1.380_648_52e023;
    public static final double ELECTRON_MASS    = 9.109_383_56e-31;
}

````
정적 임포트를 하면 상수 이름만으로 사용이 가능하다.
````java
import static constantutilityclass.PhysicalConstants.*;

public class Test{
    double atoms(double mols){
        return AVOGADROS_NUMBER * mols;
    }
}
```` 