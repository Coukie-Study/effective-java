# int 상수 대신 열거 타입을 사용하라.

### 정수 열거 패턴
````java
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;

public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
````
### 정수 열거 패턴의 단점
- 값이 똑같으면 의미가 다른 변수도 동등연산자(==)로 비교하면 true를 반환한다.
- 별도 네임스페이스를 지원하지 않기 때문에 접두어(_)를 사용하여 충돌을 방지해야 한다.
- 정수 열거 패턴을 사용한 프로그램은 클라이언트가 컴파일하면 그 값이 클라이언트 파일에 그대로 새겨 지기 때문에<br>
상수의 값이 바뀌면 반드시 다시 컴파일 해야 한다.
- 문자열로 출력하기 까다롭다. 디버거로 살펴보면 변수 명이 아닌 단순한 값으로 출력된다.
- 같은 그룹의 상수를 한바퀴 순회하는 방법도 마땅치 않다.

### 열거 타입
열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다.<br>
열거 타입 자체는 클래스이며, 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다<br>
열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않는다.<br>
싱글턴 타입은 원소가 하나뿐 열거 타입이라 할 수 있다.

````java
public enum Apple {FUJI, PIPPIN, GRANNY_SMITH}
public enum Orange {NAVEL, TEMPLE, BLOOD}

public class Apple{
    public static final Apple FUJI = new Apple();
    public static final Apple PIPPIN = new Apple();
    public static final Apple GRANNY_SMITH = new Apple();
    private Apple(){}
}
````

### 열거 타입 장점
- 열거 타입은 컴파일 타임 타입 안전성을 제공한다.
- 열거 타입에는 각자의 네임스페이스가 있다.
- 열거 타입에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일 하지 않아도 된다. 
- 열거 타입의 toString 메서드는 출력하기 적합한 문자열을 내어 준다.
- 열거 타입은 임의의 메서드나 필드를 추가할 수 있고 임의의 인터페이스를 구현하게할 수 있다.

````java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6),
    EARTH(5.975e+24, 6.378e6),
    MARS(6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN(5.685e+26, 6.027e7),
    URANUS(8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    private final double mass;           // 질량(단위: 킬로그램)
    private final double radius;         // 반지름(단위: 미터)
    private final double surfaceGravity; // 표면중력(단위: m / s^2)

    // 중력상수(단위: m^3 / kg s^2)
    private static final double G = 6.67300E-11;

    // 생성자
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass() {
        return mass;
    }

    public double radius() {
        return radius;
    }

    public double surfaceGravity() {
        return surfaceGravity;
    }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;  // F = ma
    }
}
````

### 열거 타입 유의사항
- 만약 상수를 제거하면 제거된 상수를 이용하는 클라이언트는 다시 컴파일 하였을때 오류가 발생한다.
- 열거 타입을 선언한 클래스 혹은 그 패키지에서만 유용한 기능은 private이나 package-private(default) 메서드로 구현한다.
- 널리 쓰이는 열거 타입은 톱레벨 클래스로 만들고(자체적인 클래스) 특정 톱레벨 클래스에서만 쓰인다면 해당 클래스의 맴버 클래스로 만들라.

### 열거 타입의 상수마다 다른 연산을 하는 경우
````java
public enum Operation{
    PLUS, MINUS, TIMES, DIVIDE;
    
    // 상수가 뜻하는 연산을 수행한다.
    public double apply(double x, double y){
        switch(this){
            case PLUS:  return x + y;
            case MINUS:  return x - y;
            case TIMES:  return x * y;
            case DIVIDE:  return x / y;
        }
        throw new AssertionError("알 수 없는 연산 : " + this);
    }   
}
````

````java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    @Override public String toString() { return symbol; }
    public abstract double apply(double x, double y);
}
````
### fromString 구현
열거 타입에는 상수 이름을 입력받아 그 이름에 해당하느느 상수를 반환해 주는 valueOf(String) 메서드가 자동 생성된다.
````java
//true
Planet.valueOf("EARTH") == Planet.EARTH
````
위와 비슷하게 toString 메서들 재정의하려거든, toString이 반환하는 문자열을 해당 열거 타입 상수로 변환해주는 fromString 메서드도 고려하는것이 좋다.

````java
private static final Map<String, Operation> stringToEnum = 
        Stream.of(values()).collect(
            toMap(Object::toString, e -> e));

// 지정한 문자열에 해당하는 Operation을 (존재한다면) 반환한다.
public static Optional<Operation> fromString(String symbol){
    return Optional.ofNullable(stringToEnum.get(symbol));
}   
````
- values()를 이용하여 상수들을 순회하여 하나씩 map에 추가 시켜줄 수 있지만 <br>
Stream을 활용하여 좀더 쉽게 구현하였다.
- 각각의 상수의 생성 시점에 Map에 추가해도 괜찮지 않냐고 생각 할 수 있지만 그 시점에는<br>
각각의 상수가 생성 전이므로 NullPointerException이 발생한다.
- fromString이 Optional<Operation>을 반환하는 이유는 주어진 문자열에 해당하는 상수가 존재하지 않을수도 있음을 클라이언트에게 알리기 위함이다.


### 상수별 메서드 구현에서 상수끼리 코드의 공유가 필요한 경우
````java
enum PayrollDay {
    MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND), SUNDAY(WEEKEND);
    
    private static final int MINS_PER_SHIFT = 8 * 60;
    
    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;
    
        int overtimePay;
        switch (this){
            case SATURDAY: case SUNDAY: // 주말
                overtimePay = basePay / 2;
                break;
            default: // 주중
                overtimePay = minutesWorked <= MINS_PER_SHIFT ?
                    0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
        }
    }
}
````
### 위 방식의 문제점
- 휴가와 같은 새로운 값이 열거 타입에 추가 되면 case 문에 추가해줘야 한다.

### 다른 방식
- 중복되는 코드를 그냥 중복해서 넣는다.
- 도우미 메서드를 만들어서 각 상수가 자신에게 필요한 메서드를 적절히 호출한다.
- 기본적인 메서드를 구현하고 변경해야 하는 상수에만 재정의해서 사용한다. 

### 전략 열거 타입 패턴
````java
enum PayrollDay {
    MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND), SUNDAY(WEEKEND);
 
    private final PayType payType;

    PayrollDay(PayType payType) { this.payType = payType; }

    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    // 전략 열거 타입
    enum PayType {
        WEEKDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 :
                        (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };

        abstract int overtimePay(int mins, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }
}
````