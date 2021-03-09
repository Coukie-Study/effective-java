# null이 아닌, 빈 컬렉션이나 배열을 반환하라

```java
private final List<Cheese> cheesesInStock = ...;

public List<Cheese> getCheeses(){
	return cheesesInStock.IsEmpty() ? null
    : new ArrayList<>(cheesesInStock);
}
```

매장 안의 모든 치즈 목록을 반환한다. 단, 하나도 없다면 null을 반환한다.

이 코드처럼 null을 반환한다면, 클라이언트는 이 null 상황을 처리하는 코드를 추가로 작성해야 한다.

```java
List<Cheese> cheeses = shop.getCheeses();
if(cheeses !=null && cheeses.contains(Cheese.STILTON))
		System.out.println("좋았어, 바로 그거야");
```

- 컬렉션이나 배열같은 컨테이너가 비어있을 때 null을 반환하는 메서드를 사용할 때면 항상 이와 같은 방어 코드를 넣어줘야 한다.

- 클라이언트에서 방어 코드를 빼먹으면 오류가 발생할 수 있다. 

- 그리고 null을 반환하려면 반환하는 쪽에서도 이 상황을 특별히 취급해줘야 해서 코드가 더 복잡해진다.

  

### null을 반환하는 쪽이 낫다는 주장도 있다.

빈 컨테이너를 할당하는 데도 비용이 드니 null을 반환하는 쪽이 낫다는 주장도 있다. 이는 두 가지 면에서 틀린 주장이다.

- 성능분석 결과 이 할당이 성능저하의 주범이라고 확인되지 않는 한 이 정도의 성능차이는 신경 쓸 수준이 못 된다.
- 빈 컬렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다.
- 매번 똑같은 빈 '불변' 컬렉션을 반환하면 된다.

```java
public List<Cheese> getCheeses(){
		return cheesesInStock.isEmpty() ? Collections.emptyList()
				: new ArrayList<>(cheesesInStock)
}
```

최적화가 필요하다고 판단될 때 사용하자.



### 정리

null이 아닌, 빈 배열이나 컬렉션을 반환하라. null을 반환하는 API는 사용하기 어렵고 오류 처리 코드도 늘어난다. 그렇다고 성능이 좋은것도 아니다.







