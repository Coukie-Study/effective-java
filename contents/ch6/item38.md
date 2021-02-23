# 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

- 열거 타입은 거의 모든 상황에서 타입 안전 열거 패턴보다 우수하다.

```java
// 타입 열거 패턴

public class Type {
    private final String type;
    private Type(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }

    public static Type club = new Type("club");
    public static Type heart = new Type("heart");
    public static Type diamond = new Type("diamond");
    public static Type spade = new Type("spade");
}
```

- 단 Enum 타입은 확장할 수 없다는 점에서 유일한 단점을 가진다.
- 하지만 대부분 상황에서는 열거 타입을 확장하는 것은 좋지 않다.
    - 확장한 타입의 원소와 기반 타입의 원소의 비교 시 애매함
    - 기반 타입과 확장한 타입을 모두 순회할 방법이 존재하지 않음
    - 확장성을 높이기 위해서는 고려할 요소가 늘어나 설계와 구현이 복잡해진다.

- 단 예외적인 상황이 있는데 연산 코드를 구현하는 경우이다.
    - API가 기본적으로 제공하는 연산 이외에 사용자 확장 연산을 추가할 수 있도록 열어줘야 할 떄가 있다.
    - 이 경우, 연산 코드를 정의하는 인터페이스를 정의하고 열거타입이 이 인터페이스를 구현하도록 한다.

```java
public interface Operation {
    double apply(double x, double y);
}
```

```java
public enum BasicOperation implements Operation {
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

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override public String toString() {
        return symbol;
    }
}
```

- 연산의 타입으로 인터페이스를 활용한다면 확장할 수 없는 BasicOperation 대신에 새로운 열거 타입을 정의해 기본 타입을 대체할 수 있다.

```java
public enum ExtendedOperation implements Operation {
    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        public double apply(double x, double y) {
            return x % y;
        }
    };
    private final String symbol;
    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }
    @Override public String toString() {
        return symbol;
    }
}
```
