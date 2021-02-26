# 익명 클래스보다는 람다를 사용하라

### 익명클래스

- 클래스의 선언과 객체의 생성을 동시에 하기 때문에 단 한번만 사용될 수 있고, 오직 하나의 객체만을 생성할 수 있는 일회용 클래스
  - 이름이 없기 때문에 생성자를 가질 수 없다.
  - 단 하나의 클래스를 상속받거나 단 하나의 인터페이스 만들 구현할 수 있다.

예전에는 자바에서 함수 타입을 표현할 때 추상메서드를 하나만 담은 인터페이스를 사용했다. 이런 인터페이스의 인스턴스를 함수 객체라고 한다. 함수 객체를 만드는 주요 수단은 익명 클래스가 되었다.

다음 코드를 예로 살펴보자. 문자열을 길이순으로 정렬하는데, 정렬을 위한 비교함수로 익명 클래스를 사용한다.

```java
//익명클래스의 인스턴스를 함수 객체로 사용 - 낡은 기법이다.
Collections.sort(words, new Comparator<String>() {
        public int compare(String s1, String s2) {
            return Integer.compare(s1.length(), s2.length());
        }
});
```

과거 객체 지향 디자인 패턴에는 익명클래스면 충분했다. 하지만 익명 클래스 방식은 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않았다.



### 람다

함수인데 함수를 따로 만들지 않고 코드 한줄에 함수를 써서 그것을 호출하는 방식

```
(매개변수) -> {함수몸체}
```

간단한 예제

```java
public interface Calculator {
    int cal(int num1,int num2);
}
```

```java
public static void main(String[] args) {
        Calculator cal = (int num1,int num2) -> {return num1+num2;};
        System.out.println(cal.cal(1,2));
    }
```



```java
//람다식을 함수 객체로 사용 - 익명 클래스 대체
Collections.sort(words,
                (s1,s2)->Integer.compare(s1.length(),s2.length()));
```

매개변수(s1,s2), 반환값의 타입에 대한 언급이 없는데 이것은 컴파일러가 추론해준것이다. 타입을 명시해야 코드가 더 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자.



```java
//상수별 클래스 몸체와 데이터를 사용한 열거 타입
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}
```

```java
//람다를 인스턴스 필드에 저장해 상수별 동작을 구현한 열거타입
public enum Operation {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> (x / y));

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
```

람다 기반 Operation 열거 타입을 보면 상수별 클래스 몸체는 더 이상 사용할 이유가 없다고 느낄지 모르지만 그렇지 않다.

메서드나 클래스와 달리, 람다는 이름이 없고 문서화도 못한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수 가 많아지면 람다를 쓰지 말아야 한다.











