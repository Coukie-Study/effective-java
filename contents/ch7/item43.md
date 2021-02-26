# 람다보다는 메서드 참조를 사용하라

람다가 익명 클래스보다 나은 점 중에서 가장 큰 특징은 간결함이다. 그런데 자바에는 함수 객체를 심지어 람다보다도 더 간결하게 만드는 방법이 있으니, 바로 메서드 참조이다.

### 메서드 참조

```java
public class Calculator {
    public int Add(int x, int y) {
        return x + y;
    }
}
```

```java
 public static void main(String[] args) {
        Calculator calculator = new Calculator();
        IntBinaryOperator operator;
        operator=calculator::Add;		//메서드 참조
        System.out.println(operator.applyAsInt(1,2));
}
```



다음 코드는 임의의 키와 Integer값의 매핑을 관리하는 프로그램의 일부다.

```java
map.merge(key, 1, (count, incr) -> count +incr);
```

merge 메서드는 키, 값, 함수를 인수로 받으며, 주어진 키가 맵 안에 아직 없다면 주어진 쌍을 그대로 저장하고 반대로 키가 있다면 함수를 현재값과 주어진 값에 적용한 다음, 그 결과로 현재 값을 덮어쓴다.

깔끔해보이는 코드지만 아직도 거추장스러운 부분이 남아있다. 매개변수인 count와 incr은 크게 하는 일 없이 공간을 꽤 차지한다. 자바 8이 되면서 Integer클래스는 이 람다와 기능이 같은 정적 메서드 sum을 제공하고 있고 람다 대신 이 메서드의 참조를 전달하면 똑같은 결과를 더 보기 좋게 얻을 수 있다.

```java
map.merge(key, 1, Integer::sum)
```



### 람다, 메서드 참조의 사용

- 메서드 참조를 사용하는 편이 보통은 더 짧고 간결하므로, 람다로 구현했을 때 너무 길거나 복잡하다면 메서드 참조가 좋은 대안이 되어준다.

- 다만, 항상 메서드 참조가 적절한 것은 아니다

  ```java
  //참조할 메서드가 GoshThisClassNameIsHumongous클래스 안에 있다고 할 때
  
  service.execute(GoshThisClassNameIsHumongous::action);	//메서드 참조
  
  service.execute(() -> action());	//람다를 사용할 때
  ```



### 메서드 참조 유형

- 정적메서드를 가치키는 참조
- 인스턴스 메서드를 참조하는 유형
  - 수신객체(receiving object)를 특정하는 한정적(bound) 인스턴스 메서드 참조
  - 수신객체(receiving object)를 특정하지 않는 비한정적(unbound) 인스턴스 메서드 참조



### 정리

메서드 참조는 람다의 간단명료한 대안이 될 수 있다. 메서드 참조 쪽이 짧고 명확하다면 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하라.