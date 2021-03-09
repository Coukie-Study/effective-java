# 다중정의는 신중히 사용하라

```java
public class CollectionClassifier {
    public static String classify(Set<?> s){
        return "집합";
    }

    public static String classify(List<?> lst){
        return "리스트";
    }

    public static String classify(Collection<?> c){
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {new HashSet<String>(),new ArrayList<BigInteger>(),new HashMap<String, String>().values()};
        for(Collection<?> c :collections){
            System.out.println(classify(c));
        }
    }
}
```

"집합", "리스트", "그 외"를 차례로 출력할 것 같지만 실제로는 "그 외"만 세 번 연달아 출력한다.

다중정의된 세 classify 중 어느 메서드를 호출할지가 컴파일 타임에 정해지기 때문이다.

컴파일 타임에는 for 문 안의 c는 항상 Collection< ? > 타입이다. 런타임에는 타입이 매번 달라지지만, 호출할 메서드를 선택하는 데는 영향을 주지 못한다.



### 직관과 어긋나는 이유

재정의한 메서드는 동적을 선택되고 다중정의한 메서드는 정적으로 선택되었기 때문이다.

메서드를 재정의한다면 런타임에 어떤 메서드를 호출할지가 결정된다.



### 재정의된 메서드 호출 메커니즘

```
class Wine {
    String name() {
        return "포도주";
    }
}

class SparklingWine extends Wine {
    @Override
    String name() {
        return "발포성 포도주";
    }
}

class Champagne extends SparklingWine {
    @Override
    String name() {
        return "샴페인";
    }
}

public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(new Wine(), new SparklingWine(), new Champagne());
        for (Wine wine : wineList) {
            System.out.println(wine.name());
        }
    }
}
```

예상한 것처럼 이 프로그램은 "포도주", "발포성 포도주", "샴페인"을 차례로 출력한다.

for 문에서의 컴파일타임 타입이 모두 Wine인 것에 무관하게 항상 가장 하위에서 정의한 재정의 메서드가 실행되는 것이다.



### 다중정의가 혼동을 일으키는 상황을 피해야 한다.

- 안전하고 보수적으로 가려면 매개변수 수가 같은 다중정의는 만들지 말자.
- 가변인수를 사용하는 메서드라면 다중정의를 아예 하지 말아야 한다.
- 다중정의하는 대신 메서드 이름을 다르게 지어주는 방법도 있다.



### 오토박싱으로 인한 오류

```java
public class SetList {
    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<>();
        List<Integer> list = new ArrayList<>();
        for (int i = -3; i < 3; i++) {
            set.add(i);
            list.add(i);
        }
        for (int i = 0; i < 3; i++) {
            set.remove(i);
            list.remove(i);
        }
        System.out.println(set + " " + list);
    }
}
```

```
//출력값
[-3, -2, -1] [-2, 0, 2]
```

set.remove(i)의 시그니처는 remove(Object)다. 다중정의된 다른 메서드가 없으니 집합에서 0, 1, 2를 제거한다.

그러나 list.remove(i)는 다중정의된 remove(int index)를 선택한다.이 때 remove는 지정한 위치의 원소를 제거하는 기능을 수행한다. 따라서 차례대로 0번째, 1번째, 2번째 원소를 제거한다.

이 문제는 list.remove의 인수를 Integer로 형변환하여 올바른 다중정의 메서드를 선택하게 하면 해결된다.

```java
for (int i = 0; i < 3; i++) {
            set.remove(i);
            list.remove((Integer)i);
        }
```



### 정리

- 프로그래밍 언어가 다중정의를 허용한다고 해서 다중정의를 꼭 활용하라는 뜻은 아니다.
- 일반적으로 매개변수 수가 같을 때는 다중정의를 피하는 것이 좋다.
- 기존 클래스를 수정해 새로운 인터페이스를 구현해야 할 때는 같은 객체를 입력받는 다중정의 메서드들이 모두 동일하게 동작하도록 만들자.