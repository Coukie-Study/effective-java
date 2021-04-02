# 지연 초기화는 신중히 사용하라

- 지연초기화는 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법이다.
- 그래서 값이 전혀 쓰이지 않으면 초기화도 결코 일어나지 않는다.
- 지연 초기화는 주로 최적화 용도로 쓰이지만, 클래스와 인스턴스 초기화 때 발생하는 위험한 순환 문제를 해결하는 효과도 있다.



### 지연초기화는 필요할 때까지는 하지마라

- 지연 초기화는 양날의 검이다. 클래스 혹은 인스턴스 생성 시의 초기화 비용은 줄지만 그 대신 지연 초기화 하는 필드에 접근하는 비용은 커진다.
- 지연 초기화하려는 필드들 중 결국 초기화가 이뤄지는 비율에 따라, 실제 초기화에 드는 비용에 따라, 초기화된 각 필드를 얼마나 빈번히 호출하느냐에 따라 지연 초기화가 실제로는 성능을 느려지게 할 수도 있다.



### 지연초기화가 필요한 경우

- 해당 클래스의 인스턴스 중 그 필드를 사용하는 인스턴스의 비율이 낮은 반면, 그 필드를 초기화하는 비용이 크다면 지연 초기화가 제 역할을 해줄 것이다.
- 실제로 그런지 확인해 볼 수 있는 유일한 방법은 지연 초기화 적용 전후의 성능을 측정해보는 것이다.



### 대부분의 상황에서 일반적인 초기화가 지연 초기화보다 낫다.

```java
//인스턴스 필드를 초기화하는 일반적인 방법
private final FieldType field = computeFieldValue();
```

- 지연 초기화가 초기화 순환성을 깨뜨릴 것 같으면 synchronized를 단 접근자를 사용하자.

```java
private FieldType field;

private synchronized FieldType getField(){
  	if(field == null)
      	field = computeFieldValue();
  	return field;
}
```



### 성능 때문에 정적 필드를 지연 초기화 해야 한다면 지연 초기화 홀더 클래스 관용구를 사용하자.

```java
//정적 필드용 지연 초기화 홀더 클래스 관용구
private static class FieldHolder{
  static final FieldType field = computeFieldValue();
}

private static FieldType getField(){ return FieldHolder.field; }
```

- getField가 처음 호출되는 순간 FieldHolder.field가 처음 읽히면서, 비로소 FieldHolder 클래스 초기화를 촉발한다.
- getField 메서드가 필드에 접근하면서 동기화를 전혀 하지 않으니 성능이 느려지지 않는다.
- 일반적인 VM은 오직 클래스를 초기화 할때만 필드 접근을 동기화 할것이다.
- 클래스 초기화가 끝난 후에는 VM이 동기화 코드를 제거하여, 그 다음부터는 아무런 검사나 동기화 없이 필드에 접근하게 된다.



### 성능 때문에 인스턴스 필드를 지연 초기화 해야 한다면 이중검사 관용구를 사용하라

```java
//인스턴스 필드 지연 초기화용 이중검사 관용구
private volatile FieldType field;//필드가 초기화된 후로는 동기화 하지 않으므로 volatile로 선언

private FieldType getField(){
		FieldType result = field;
  	if(result != null) //첫 번째 검사(락 사용 안함), 동기화 없이 검사
	     return result;

  	synchrnoized(this){	//두 번째 검사(락 사용), 동기화하여 검사
      	if(field == null)
          field = computeFieldValue();
      	return field;
    }
}
```



### 정리

- 대부분의 필드는 지연시키지 말고 곧바로 초기화 해야 한다.
- 인스턴스 필드에는 이중검사 관용구를, 정적 필드에는 지연 초기화 홀더 클래스 관용구를 사용하자.