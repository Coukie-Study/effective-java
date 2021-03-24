# 필요 없는 검사 예외 사용은 피하라.

### 검사 예외를 남용하면 사용하기 불편한 API가 될 수 있다.
- 검사 예외는 발생한 문제를 프로그래머가 try catch 문을 통해 코드 상에서 직접 처리해야 하므로 안전성을 높일 수 있지만
<br> 그만큼 사용하기 불편한 API가 될 수 있다.
- 검사 예외를 던지는 메서드는 스트림 안에서 직접 사용할 수 없다.
- API를 제대로 사용해도 발생할 수 있는 예외거나, 프로그래머가 의미있는 조치를 취할 수 있는 경우를 제외하곤 비검사 예외를 사용하는것이 좋다.
- 메서드가 단 하나의 검사 예외만 던질때 그 검사예외로 인해 try catch 문을 써야하고, 스트림을 사용 못하게 되므로
<br> 검사 예외의 사용을 피하는것을 더욱 고려해야 한다.
### 검사 예외를 회피하는 방법
#### 비검사 예외 사용
````java
} catch (TheCheckedException e){
    throw new AssertionError();
}

} catch (TheCheckedException e){
    e.printStackTrace();
    System.exit(1);
}
````

#### Optional 사용
Optional을 사용하여 예외가 발생하는 경우 예외를 던지는 대신 단순히 빈 Optional을 반환하면 된다.
<br> 하지만 이 방식은 예외가 발생한 이류를 알려주는 부가 정보를 담을 수 없다.

#### 검사 예외를 던지는 메서드를 쪼개기(예외 발생여부를 알려주는 메서드 추가)
````java
try {
    Obj.action(args);    
} catch (TheCheckedException e) {
    //do something
}
````

````java
if (obj.actionPermitted(args)) {
    obj.action(args);
} else {
    //do something
}
````
- 모든 상황에 적용할 수 없지만, 적용할 수 있다면 더 쓰기 편한 API를 제공할 수 있다.
- 위 방식은 코드가 길어져도 확실히 API의 사용이 유연해진다.<br>
- 외부 동기화 없이 여러 스레드가 접근할 수 있다면 actionPermitted와 action 호출 사이에 객체의 상태가 변할 수 있기에 조심해야 한다.
