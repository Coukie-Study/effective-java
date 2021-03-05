# 매개변수가 유효한지 검사하라

- 오류는 가능한 한 빨리 발생한 곳에서 잡아야 한다.
- 메서드가 실행되기 전 매개변수를 확인한다면 잘못된 값이 들어왔을 때 즉각적이고 깔끔한 예외 처리 가능

## 매개변수 검사를 제대로 하지 않을 때 문제점

- 메서드 수행 중간에 모호한 예외를 던지거나 잘못된 결과를 반환, 혹은 객체를 잘못된 상태에 놓아 알 수 없는 시점에 오류를 낸다. 즉 실패 원자성을 어기는 결과를 가져온다.

## 문서화

- public과 protected 메서드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화해야 한다.(@throws)
- 보통 IllegalArgumentException, IndexOutOfBoundsException, NullPointerException 중 하나
- 매개 변수의 제약을 문서화 한다면 제약을 어겼을 때 발생하는 예외도 같이 기술해야 한다.

```java
/**
 * ...
 * @throws ArithmeticException m이 0보다 작거나 같으면 발생한다.
 */
public BigInteger mod(BigInteger m) {
    ...
}
```

- 메서드 수준에 생략되어 있는 예외가 있을 수 있는데 이 경우는 클래스 수준 주석을 통해 모든 public 메서드에 적용되는 예외를 기술된 경우일 수 있다.

### Objects 메서드 활용

- Objects::requeireNonNull 메서드를 통해 null 검사를 효율적으로 수행할 수 있으며 예외 메시지를 지정할 수 있다.
- 자바 9에서는 checkFromIndexSize, checkFromToIndex, checkIndex도 추가되어 index 관련 검사도 가능

## assert

- 공개되지 않은 메서드라면 패키지 제작자가 메서드가 호출되는 상황을 통제할 수 있다. 따라서 유효한 값만 넘겨지리라는 것을 보증할 수 있다.
- public이 아닌 메서드라면 assert를 통해 매개변수 유효성을 검증할 수 있다.
- assert는 실패하면 AssertionError를 던지며 런타임에는 아무런 성능 저하가 없다.
- 즉 assert는 테스트 시 -ea 옵션을 통해 디버깅 용도로 사용된다.

## 그 밖의 주의점

- 메서드가 직접 사용하지는 않으나 나중에 쓰기 위해 저장하는 매개변수는 특히 더 신경 써서 검사해야한다.
    - 생성자가 그 예

## 예외

- 다만 유효성 검사 비용이 지나치게 높거나 실용적이지 않을 때, 혹은 계산과정에서 암묵적으로 검사가 수행될 때 생략될 수 있다.
- ex) Collections.sort(List)
    - 객체는 모두 비교 가능한지를 우선 검증해야 하지만 정렬 과정에서 비교 가능하지 않다면 예외를 던지기 때문에 실익이 없다.
- 다만 실패 원자성을 해칠 수 있으니 주의
- 계산 과정에서 필요한 유효성 검사가 유효성 검사가 이뤄지지만 실패했을 때 잘못된 예외를 던지기도 한다. 이럴 경우 예외 번역 관용구를 사용해 API 문서에 기재된 예외로 번역해줘야 한다.(ex. NoSuchElementException, IndexOutOfBoundException)