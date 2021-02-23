# 비트 필드 대신 EnumSet을 사용하라

## 비트 필드

- 예전에는 열거 값들을 집합으로 사용할 경우 각 상수에 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴을 사용해왔다.

```java
public class Text {
    public static final int STYLE_BOLD = 1 << 0; // 1(1)
    public static final int STYLE_ITALIC = 1 << 1; // 2(10)
    public static final int STYLE_UNDERLINE = 1 << 2; // 4(100)
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8(1000)
}
```

- 다음과 같은 식으로 비트별 OR 연산을 통해 여러 상수를 하나의 집합으로 표현 가능하며 이렇게 만들어진 집합을 비트 필드라 한다.
- `text.applyStyles(STYLE_BOLD | STYLE_ITALIC)` → 0011
- 비트 필드를 사용하면 비트별 연산을 사용해 합집합과 교집합 같은 집합 연산을 효율적으로 수행할 수 있다.
- But, 비트 필드는 전 아이템에서 나타난 정수 열거 상수의 단점을 그대로 지닌다.
    - 또한 비트 필드 값을 그래도 출력하면 단순한 정수 열거 상수를 출력할 때보다 해석하기가 훨씬 까다롭다.
        - 0011 같은 숫자를 바로 Text의 스타일로 받아들이기는 힘들 것이다.
    - 최대 몇 비트가 필요한지 미리 계산하여 타입을 선택해야 한다.

## EnumSet

- 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현해주는 자료구조
- Set 인터페이스를 구현하며, 타입 안전, 다른 Set 구현체와도 함께 사용할 수 있다.
- 내부 구현은 비트 벡터로 구현되었으며 비트 필드와 비슷한 성능을 보여준다.

```java
// RegularEnumSet.java

// Bit vector representation of this set. The 2^k bit indicates the presence of universe[k] in this set.
private long elements = 0L;
```

- 비트를 직접 다루지 않아도 되는 이점을 얻을 수 있다.

```java
public class Text {
    public enum Style {BOLD, ITALIC, UNDERLINE, STRIKETHROUGH}

    // Any Set could be passed in, but EnumSet is clearly best
    public void applyStyles(Set<Style> styles) {
        System.out.printf("Applying styles %s to text%n",
                Objects.requireNonNull(styles));
    }

    // Sample use
    public static void main(String[] args) {
        Text text = new Text();
        text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
    }
}
```

- `text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));`으로 보다 깔끔하게 메서드를 사용할 수 있다.
