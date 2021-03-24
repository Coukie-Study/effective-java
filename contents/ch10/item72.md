# 표준 예외를 사용하라.
### 표준 예외 사용의 장점
- API 사용자로 하여금 사용하기 쉬워진다
- 예외 수가 적을수록 메모리 사용량이 줄어든다.

### 널리 재사용되는 표준 예외
- IllegalArgumentException
    - 호출자가 인수로 부적절한 값을 넘길 때 던지는 예외
    - ex) 반복횟수에 사용되는 매개변수에 음수값을 건네는 경우
- IllegalStateException
    - 대상 객체의 상태가 호출된 메서드를 수행하기 적합하지 않을때
    - ex) 제대로 초기화되지 않은 객체를 사용하려 하는 경우
- NullPointerException
    - null 값을 허용하지 않는 메서드에 null을 건내는 경우
- IndexOutOfBoundsException 
    - 인덱스 범위를 넘어선 경우
- ConcurrentModificationException
    - 단일 스레드에서 사용하려고 설계한 객체를 여러스레드가 동시에 수정하려 할때 던지는 예외
    - 이러한 문제가 생길 가능성 정도를 알려주는데 사용된다.
- UnsupportedOperationException
    - 클라이언트가 요청한 동작을 대상 객체가 지원하지 않을때 던지는 예외
    - ex) 원소를 넣을 수만 있는 List를 구현하였는데 누군가 remove 메서드를 호출하는 경우
- ArithmeticException, NumberFormatException
    - 복소수, 유리수를 다루는 객체를 작성할때 사용하는 예외

### 표준 예외 사용시 주의사항
- Exception, RuntimeException, Throwable, Error는 직접 재사용 하지말자.
<br> 이 예외들은 다른 예외들의 상위 클래스이므로 여러 성격의 예외들을 포괄하여 안정적으로 테스트가 불가능하다.
- API문서를 참고해 사용하려는 표준 예외가 어떤 상황에서 던져지는지 확인하고 사용하자.
- 표준 예외를 확장하여도 좋지만 예외는 직렬화가 가능해 내가 확장한 예외의 직렬화는 많은 부담이 따른다.<br>
그러므로 왠만하면 새로 만들지 않아야 한다.

### IllegalArgumentException과 IllegalStateException을 구분하는 방법
어떤 인수값이 들어오든 실패할거라면 IllegalArgumentException을 사용하고
<br> 아니라면 IllegalStateException을 사용하면 된다. 