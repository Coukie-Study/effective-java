# 추상화 수준에 맞는 예외를 던져라.
수행하려는 일과 관련이 없어 보이는 예외가 튀어나오면 당황하게 된다.
<br> 이를 문제를 피하려면 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던져야 한다.
이를 예외 번역이라 한다.

#### 예외 번역 예시
````java
try{
    ...
} catch (LowerLevelException e){
    throw new HigherLevelException(...);
}
````

````java
// AbstractSequentialList.java

 public E get(int index) {
        try {
            return listIterator(index).next();
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }
````

#### 예외 연쇄
예외를 번역할 때, 저수준 예외가 디버깅에 도움이 된다면 예외 연쇄를 사용하는게 좋다.
<br> 예외 연쇄란 문제의 근본 원인인 저수준 예외를 고수준 예외에 실어 보내는 방식이다.
<br> 그러면 별도의 접근자 메서드를 통해 필요하면 언제든 저수준 예외를 볼 수 있다.

````java
try{
    ...
} catch (LowerLevelException cause){
    throw new HigherLevelException(cause);
}
````

고수준 예외의 생성자는 상위 클래스의 생성자에 이 원인(LoweLevelException)을 건네주어,
<br> 최종적으로 Throwable(Throwable) 생성자까지 건네지게 한다.

````java
class HigherLevelException extends Exception{
    HigherLevelException(Throwable cause){
        super(cause);
    }
}
````

### 유의사항
- 없는것보단 낫지만 예외 번역을 남용하면 안된다.
- 가능하다면 저수준 메서드가 반드시 성공하도록 하여 아래 계층에서는 예외가 발생하지 않도록 하는것이 최선이다.
    - 상위 계층 메서드의 매개변수 값을 아래 계층 메서드로 건네기 전에 미리 검사하는 방법은 하나의 해결법이다.
- 차선책으로 아래 계층의 예외를 피할 수 없다면, 상위 계층에서 조용히 처리하여 처리한 예외는 로깅을 활용하여 남겨두면 좋다.
