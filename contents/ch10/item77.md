# 예외를 무시하지 말라
### 예외를 무시하는 방법
````java
try {
    ...
} catch (SomeException e){
}
````
- 위처럼 catch 문을 비워두면 해당 예외가 발생했는지 알 수도 없이 다음 코드가 실행된다.
- 빈 catch 문을 본다면 반드시 유의해서 확인해야 한다.

### 예외를 무시하는 경우

- FileInputStream을 닫는 경우 파일의 상태를 변경하지 않았으니 복구 할 것이 없고
<br> 필요한 정보는 이미 다 읽었으니 남은 작업을 중단할 이유도 없다.

- 예외를 무시하려면 catch 블록 안에 그렇게 결정한 이유를 주석으로 남기고 예외 변수의 이름도 ignored로 바꾸자.
````java
int numColors = 4;
try{
    numColors = f.get(1L, TimeUnit.SECONDS);
}catch (TimeoutException | ExecutionException ignored){
    // 기본값을 사용한다(색상 수를 최소화하면 좋지만, 필수는 아니다).
}
````
