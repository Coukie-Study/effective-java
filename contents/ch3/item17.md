# 변경 가능성을 최소화하라

## 불변 클래스

---

- 인스턴스의 내부 값을 수정할 수 없는 클래스
- 객체가 파괴되는 순간까지 정보가 변하지 않는다.
- String, BigInteger, BigDecimal...
- 불변 클래스는 가변 클래스보다 설계, 구현, 사용이 용이
- 오류가 생길 여지가 적고 안전하다.

## 불변클래스 5가지 규칙

---

1. 객체의 상태를 변경하는 메서드를 제공하지 않는다.

    ```java
    // don't
    public void setA(String a) {
        this.a = a;
    }
    ```

2. 클래스를 확장할 수 없도록 한다.
    - 하위 클래스에서 부주의하게 혹은 나쁜 의도로 객체의 상태를 변하게 만드는 사태를 막아준다.
3. 모든 필드를 final로 선언한다.
    - 시스템이 강제하는 수단을 이용해 설계자의 의도를 명확히 드러내는 방법
    - 새로 생성된 인스턴스를 동기화 없이 다른 스레드로 건네도 문제없이 동작하게끔 보장하는 데 필요
4. 모든 필드를 private로 선언한다.
    - 필드가 참조하는 가변 객체에 대한 접근해 수정하는 것을 막아준다.
5. 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다.
    - 클라이언트에서 내부 객체의 참조값을 직접 얻어서는 안된다. 방어적 복사를 수행해라.

ex) Complex

```java
public final class Complex {
    private final double re;
    private final double im;

    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);
    public static final Complex I = new Complex(0, 1);

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double realPart() {
        return re;
    }

    public double imaginaryPart() {
        return im;
    }

    public Complex plus(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }

    // Static factory, used in conjunction with private constructor (Page 85)
    public static Complex valueOf(double re, double im) {
        return new Complex(re, im);
    }

    public Complex minus(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }

    public Complex times(Complex c) {
        return new Complex(re * c.re - im * c.im,
                re * c.im + im * c.re);
    }

    public Complex dividedBy(Complex c) {
        double tmp = c.re * c.re + c.im * c.im;
        return new Complex((re * c.re + im * c.im) / tmp,
                (im * c.re - re * c.im) / tmp);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Complex))
            return false;
        Complex c = (Complex) o;

        // See page 47 to find out why we use compare instead of ==
        return Double.compare(c.re, re) == 0
                && Double.compare(c.im, im) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(re) + Double.hashCode(im);
    }

    @Override
    public String toString() {
        return "(" + re + " + " + im + "i)";
    }
}
```

- 각 메소드는 자신의 필드를 수정하는 것이 아니라 새로운 객체를 만들어 반환한다.(함수형 프로그래밍)
    - 부자연스럽게 느껴질 수도 있지만 위와 같은 코딩 방식은 불변의 영역이 넓어지는 효과가 있다.

## 불변 객체의 장점

---

1. 단순하다.
    - 생성되는 시점의 상태를 파괴될 때까지 유지한다.
    - 또한 모든 생성자에서 불변식을 보장한다면 다른 프로그래머는 신경쓰지 않고도 그 상태를 유지할 수 있다.
2. 근본적으로 스레드 안전하여 따로 동기화할 필요가 없다.
    - 여러 스레드에서 동시에 사용해도 훼손되지 않는다. 따라서 불변 객체는 안심하고 공유할 수 있다.
    - 따라서 상수를 제공해 재활용하거나 정적 팩토리 메서드를 통해 캐싱하여 재활용할 수 있다.
    - 방어적 복사도 불필요하다.
3. 불변 객체는 자유롭게 공유 가능할 수 있음은 물론, 불변 객체끼리는 내부 데이터를 공유할 수 있다.

    ```java
    public class BigInteger extends Number implements Comparable<BigInteger> {
        /**
         * The signum of this BigInteger: -1 for negative, 0 for zero, or
         * 1 for positive.  Note that the BigInteger zero <i>must</i> have
         * a signum of 0.  This is necessary to ensures that there is exactly one
         * representation for each BigInteger value.
         *
         * @serial
         */
        final int signum;
        
        /**
         * The magnitude of this BigInteger, in <i>big-endian</i> order: the
         * zeroth element of this array is the most-significant int of the
         * magnitude.  The magnitude must be "minimal" in that the most-significant
         * int ({@code mag[0]}) must be non-zero.  This is necessary to
         * ensure that there is exactly one representation for each BigInteger
         * value.  Note that this implies that the BigInteger zero has a
         * zero-length mag array.
         */
        final int[] mag;
            
            // ...
            public BigInteger negate() {
            return new BigInteger(this.mag, -this.signum);
        }
    }
    ```
    - mag는 비록 int배열(가변)이지만 nagate에서 생성된 객체와 원본 객체가 공유할 수 있다.

4. 객체를 만들 때 다른 불변 객체들을 구성요소로 사용하면 이점이 많다.
    - 구조가 복잡하더라도 불변식을 유지하기 수월하다. (ex. Map)
5. 불변 객체는 그 자체로 실패 원자성을 제공한다.

    ```java
    public void setHelloWorld(int value){
            this.hello=value;
            shouldThrowException();
            this.world=value;
            }
    ```

    - 불일치 상태를 가지지 않는다.

## 불변 객체의 단점

---

- 값이 다르면 반드시 독립적인 객체로 만들어야 한다.
    - BigInteger의 예에서 값이 한 비트라도 달라도 새로운 객체를 만들어야 한다.
    - 원하는 객체를 만들기까지 중간 단계의 객체를 만드는데 비싼 비용이 든다면 성능 상의 이슈가 발생할 ㅅ 있다.
    - 이를 해결하기 위해 다단계 연산을 예측해 기본 기능을 제공하는 방법이 필요
    - 클라이언트가 원하는 복잡한 연산을 정확히 예측할 수 있다면 package-private 가변 동반 클래스를 통해 해결
        - 어렵다면 public으로 제공
        - 가변 동반 클래스의 예시 : StringBuilder
- StringBuilder

    ```java
    abstract class AbstractStringBuilder implements Appendable, CharSequence {
        /**
         * The value is used for character storage.
         */
        char[] value;
    
        // ...
    }
    ```

## 불변 클래스를 만드는 몇가지 설계 방법

---

- final 클래스로 설계할 수 있지만 더 유연한 방법은 생성자를 private로 선언하고, 정적 팩토리 메서드만 제공하는 것이 있다.
    - package-private를 활용해 생성자는 내부 패키지에서 활용할 수 있지만, 외부에서는 사용할 수 없으니 사실상 final이다.
- final로 상속을 막지 않을 경우, 신뢰할 수 없는 클라이언트가 제공하는 불변 객체를 사용할 수 있기에 방어적 복사를 통해 인스턴스가 가변일 경우를 고려해야 한다.
- 불변 객체 규칙 중 '모든 필드는 final이어야 하고 어떤 메서드도 수정할 수 없어야 한다'는 부분을 완화해 '어떤 메서드도 객체의 상태 중 외부에 비치는 값을 변경할 수 없다'로 완화한다면 불변객체 안에서
  계산 비용이 큰 값을 나중에 쓰일 경우에만 계산해 성능을 향상 시킬 수 있다.

## 정리

---

1. Getter가 있다고 해서 Setter를 무조건 추가하지 말자.
    - 꼭 필요한 경우가 아니라면 클래스는 불변이어야 한다.
    - 성능 상의 이슈가 있다면 불변 클래스와 쌍을 이루는 가변 동반 클래스를 제공하자.(String, StringBuilder)
2. 불변으로 만들 수 없는 객체라도 변경할 수 있는 부분을 최소한으로 줄이자.
3. 다른 합당한 이유가 없다면 모든 필드는 private final이어야 한다.
4. 생성자는 불변식 설정이 모두 완료된, 초기화가 완벽히 끝난 상태의 객체를 생성해야 한다.
    - 특별한 이유가 없다면 어떤 초기화 메서드도 public으로 제공하면 안된다.
    - 객체를 재활용할 목적으로 상태를 다시 초기화하는 메서드도 안된다.