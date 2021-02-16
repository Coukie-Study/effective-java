# 제네릭과 가변인수를 함께 쓸 때는 신중하라

가변인수: 하나의 함수에서 매개변수를 동적으로 받을수 있는 방법

가변인수는 컴파일시 배열로 처리된다.

```java
public class varargs {
    public static void main(String[] args) {
        varargs v = new varargs();
        v.variable();
        v.variable("A");
        v.variable("A","B");
        v.variable("A","B","C");
    }
    public void variable(String... s){
        for(String st:s){
            System.out.print(st);
        }
        System.out.println();
    }
}
```

```
A
AB
ABC
```



### 가변인수와 제네릭을 함께 사용할 때의 허점

가변인수 메서드를 호출하면 가변인수를 담기위한 배열이 자동으로 하나 만들어진다.

내부로 감춰야 했을 배열을 클라이언트에 노출해서 문제가 생겼다. 그 결과 가변인수 매개변수에 제네릭이나 매개변수화 타입이 포함되면 컴파일 경고가 발생한다.



### 제네릭과 varargs를 혼용하면 타입 안전성이 깨진다

```java
static void dangerous(List<String>... stringLists) {
  List<Integer> intList = List.of(42);
 
  Object[] objects = stringLists;
  Object[0] = intList; //힙 오염 발생
  String s = stringLists[0].get(0); // 런타임시에 ClassCastException
}
```

위 코드 처럼 런타임 시 예외가 발생할 수 있기 때문에 가변인수 매개변수 배열에 값을 저장하는 것은 안전하지 않다.



### @SafeVarargs

자바 7에서는 @SafeVarargs annotation이 추가되어 제네릭 가변인수 메서드 작성자가 클라이언트 측에서 발생하는 경고를 숨길 수 있게 되었다. @SafeVarargs annotation은 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치다. 컴파일러는 이 약속을 믿고 그 메서드가 안전하지 않을 수 있다는 경고를 더 이상 하지 않는다.

- 타입안전한 경우
  - 가변인수 메서드를 호출하면 가변인수 매개변수를 담는 제네릭 배열이 만들어진다.
  - 메서드 내에서 이 배열에 아무것도 저장하지 않고, 배열의 참조가 밖으로 노출되지 않는다면 타입 안전하다.

### 자신의 제네릭 매개변수 배열의 참조를 노출하는 것은 위험하다

```java
static <T> T[] toArray(T... args) {
        return args;
    }
```

```java
static <T> T[] pickTwo(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0:
                return toArray(a, b);
            case 1:
                return toArray(b, c);
            case 2:
                return toArray(c, a);
        }
        throw new AssertionError();
    }
```

이 메서드는 제네릭 가변인수를 받는 toArray 메서드를 호출한다는 점만 빼면 위험하지 않고 경고도 내지 않는다.

이 메서드를 본 컴파일러는 toArray에 넘길 T 인스턴스 2개를 담을 가변인수 매개변수 배열을 만드는 코드를 생성한다.

이 코드가 만드는 배열의 타입은 Object[]인데, pickTwo에 어떤 타입의 객체를 넘기더라도 담을 수 있는 가장 구체적인 타입이기 때문이다.

즉 pickTwo는 항상 Object[]타입 배열을 반환한다.

```java
public static void main(String[] args) {
        String[] attributes = pickTwo("좋은", "빠른", "저렴한");	//classCastException
    }
```



### 제네릭 가변인수 매개변수를 안전하게 사용하는 메서드

```java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
  List<T> result = new ArrayList<>();
  for(List<? extends T> list : lists) {
    result.addAll(list);
  }
  return result;
}
```

위의 메서드는 안전하다. 가변인수 배열을 직접 노출 시키지 않았기 때문이다.

안전한 가변인수 메서드에는 @SaveVarargs annotation을 달아서 컴파일 경고를 없애는 것이 좋다.
