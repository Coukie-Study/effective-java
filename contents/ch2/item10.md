# 아이템10. equals는 일반 규약을 지켜 재정의하라



## 재정의 하지 않는 경우

#### 1. 각 인스턴스가 본질적으로 교유하다.

값을 표현하는게 아니라 동작하는 개체를 표현하는 클래스가 여기 해당한다.

Thread가 좋은 예로, Object의 equals 메서드는 이러한 클래스에 딱 맞게 구현되었다.

#### 2. 인스턴스의 '논리적 동치성(logical equality)'을 검사할 일이 없다.

논리적 동치성: 객체가 저장하고 있는 데이터가 동일함을 뜻한다.

#### 3. 상위클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다.

Set 구현체는 AbstractSet이 구현한 equals를 상속받아 쓰고, List구현체들은 AbstractList로 부터 Map 구현체들은 AbstractMap으로부터 상속받아 그대로 쓴다.

#### 4. 클래스가 private이거나 package-private이고 equals메서드를 호출할 일이 없다.

equals가 실수로라도 호출되는 걸 막고 싶다면 다음과 같이 구현하자.

```
@Override public boolean equals(Object o){
		throw new AssertionError(); //호출 금지!
}
```



## 재정의 해야하는 경우

객체 식별성(두 객체가 물리적으로 같은가)이 아니라 논리적 동치성을 확인해야 하는데, 상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의되지 않았을때 이다.



## equals 메서드를 재정의할 때는 반드시 일반 규약을 따라야 한다.

equals 메서드는 동치관계를 구현하며, 다음을 만족한다.

- 반사성(reflexivity): null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true이다.
- 대칭성(symmetry): null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true면 y.equals(x)도 true이다.
- 추이성(transitivity): null이 아닌 모든 참조 값 x, y, z에 대해, x.equals(y)가 true이고 y.equals(z)도 true면 x.equals(z)도 true다.
- 일관성(consistency): null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
- null-아님: null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false이다.



## Object 명세에서 말하는 동치관계란 무엇일까?

Object에서 말하는 동치관계는 쉽게 말해, 서로 같은 원소들로 이루어진 부분집합으로 나누는 연산을 말한다. 이 부분집합을 동치클래스라고 한다. equals가 쓸모 있으려면 모든 원소가 같은 동치류에 속하여 어떤 원소와도 서로 교환할 수 있어야 한다.

- X={a,b,c,a,b,c}
- A={a,a}
- B={b,b}
- C={c,c}
- A, B, C가 동치 클래스이다.



## 동치관계를 만족시키기 위한 요건

#### 1. 반사성은 단순히 말하면 객체는 자기 자신과 같아야 한다는 뜻이다.

이 요건을 어긴 클래스의 인스턴스를 컬렉션에 넣은 다음 contains 메서드를 호출하면 방금 넣은 인스턴스가 없다고 답할 것이다.

```java
public class Fruit{
  private String name;
  
  public Fruit(String name){
    this.name = name;
  }
  
  public static void main(){
    List<Fruit> list = new ArrayList<>();
    Fruit f = new Fruit("apple");
    list.add(f);
    list.contains(f); 	// false일 경우에는 반사성을 만족하지 못하는 경우
  }
}
```



#### 2. 대칭성은 두 객체는 서로에 대한 동치 여부에 똑같이 답해야 한다는 뜻이다.

```java
public final class CaseInsentiveString{
  private final String s;
  public CaseInsensitiveString(String s){
    this.s = Obejcts.requireNonNull(s);
  }
  
  @Override public boolean equals(Object o){
    if(o instanceof CaseInsensitiveString)
      return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
    if(o instanceof String) // 한방향으로만 작동한다.
      return s.equalsIgnoreCase((String) o);
    return false;
  }
}
```

```java
CaseInsentiveString cis = new CaseInsentiveString("Polish");
String s = "polish";
cis.equals(s); //true
s.equals(cis); //false
```



이 문제를 해결하려면 CaseInsentiveString의 equals를 String과 연동하겠다는 허황된 꿈을 버려야 한다.

대칭성을 만족하도록 수정

```java
@Override public boolean equals(Object o){
  retunr o instanceof CaseInsentiveString &&
    ((CaseInsentiveString) 0).s.equalsIgnoreCase(s);
  //String에 대한 instanceof 부분을 빼고 구현한다.
}
```



#### 3. 추이성은 첫 번째 객체와 두 번째 객체가 같고, 두 번째 객체와 세 번째 객체가 같다면, 첫번째 객체와 세 번째 객체도 같아야 한다는 뜻이다.



```java
//2차원에서의 점을 표현하는 클래스
public class Point{
  private final int x;
  private final int y;
  
  public Point(int x,int y){
    this.x = x;
    this.y = y;
  }
  @Override public boolean equals(Object o){
    if(!(o instance of Point))
      	return false;
    Point p = (Point)o;
    return p.x == x && p.y ==y;
  }
}
```

```java
//색상정보가 추가된 클래스
public class ColorPoint extends Point{
	private final Color color;
  
  public ColorPoint(int x, int y, Color color){
    super(x, y);
    this.color = color;
  }
}
```



비교 대상이 또 다른 ColorPoint이고 위치와 색상이 같을 때만 true를 반환하는 equals를 생각해보자.

```java
//잘못된 코드 - 대칭성 위배
@Override public boolean equals(Object o){
  if(!(o instance of ColorPoint))
    return false;
  return super.equals(o) && ((ColorPoint) o).color == color;
}
```

```java
Point p = new Point(1,2);
ColorPoint p = new ColorPoint(1, 2, Color.RED);
p.equals(cp);	//true - equals의 색상을 무시
cp.equals(p); //false - 입력 매개변수의 클래스 종류가 다름
```



ColorPoint.equals가 Point와 비교할 때는 색상을 무시하도록 하면 해결될까?

```java
//잘못된 코드 - 추이성 위배
@Override public boolean equals(Object o){
  if(!(o instance of Point))
    return false;
  
  //o가 일반 Point면 색상을 무시하고 비교한다.
  if(!(o instance of ColorPoint))
    return o.equals(this);
  
  //o가 ColorPoint이면 색상까지 비교한다.
  return super.equals(o) && ((ColorPoint) o).color == color;
}
```

이 방식은 대칭성은 지켜주지만, 추이성을 깨버린다.

```java
ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
Point p2 = new Point(1, 2);
ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);
p1.equals(p2);	//true
p2.equals(p3);	//true
p1.equals(p3);	//false
```



구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 존재하지 않는다.

이 말은 얼핏, equals 안의 instanced 검사를 getClass 검사로 바꾸면 규약도 지키고 값도 추가하면서 구체 클래스를 상속할 수 있다는 뜻으로 들린다.

```java
//잘못된 코드 - 리스코프 치환원칙 위배
@Override public boolean equals(Object o){
  if(o==null || o.getClass() != getClass())
    return false;
  Point p = (Point) o;
  return p.x == x && p.y == y;
}
```

이번 equals는 같은 구현 클래스의 객체와 비교할 때만 true를 반환한다.

하지만 리스코프 치환원칙을 위배한다.

리스코프 치환원칙: 어떤 타입에 있어 중요한 속성이라면 그 하위 타입에서도 마찬가지로 중요하다. 따라서 그 타입의 모든 메서드가 하위 타입에서도 똑같이 잘 작동해야한다.

즉 Point의 하위클래스는 정의상 여전히 Point이므로 어디서든 Point로 활용가능해야 한다.



#### 우회방법1. 상속대신 컴포지션을 사용하라

```java
public class ColorPoint{
	private final Point point;
  private final Color color;
  
  public ColorPoint(int x,int y,Color color){
    point = new Point(x, y);
    this.color = Objects.requireNonNull(color);
  }
  
  public Point asPoint(){	
    return point;
  }
  
  @Override public boolean equals(Object o){
    if(!(o instanceof ColorPoint))
      return false;
    ColorPoint cp = (ColorPoint) o;
    return cp.point.equals(point) && cp.color.equals(color);
  }
}
```

- ColorPoint - ColorPoint: ColorPoint의 equals를 이용하여 color 값까지 모두 비교

- ColorPoint - Point: ColorPoint를 asPoint() 메소드를 이용해 Point로 바꾸어서, Point의 equals를 이용해 x, y만 비교
- Point - Point: Point의 equals를 이용하여 x, y 값 모두 비교



#### 4. 일관성(Consistency)

두 객체가 같다면(어느 하나 혹은 두 객체 모두가 수정되지 않는 한) 앞으로도 영원히 같아야 한다는 뜻이다.

가변객체는 비교 시점에 따라 서로 다를 수도 혹은 같을 수도 있는 반면, 불변 객체는 한번 다르면 끝까지 달라야 한다.

equals는 항시 메모리에 존재하는 객체만을 사용한 결정적(deterministic) 계산만 수행해야 한다.



#### 5. null-아님

null-아님은 이름처럼 모든 객체가 null과 같지 않아야 한다는 뜻이다.

1) 명시적 null 검사

```java
@Override public boolean equals(Object o){
  if( o == null){
    return false;
  }
}
```



2) 묵시적 null 검사

```java
@Override public boolean equals(Obejct o){
  if(!(o instanceof MyType)) 	// instanceof 자체가 타입과 무관하게 null이면 false 반환함.
    return false;
  MyType mt = (MyType) o;
}
```



## 양질의 equals 메서드 구현하는 4단계

#### 1. ==연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.

자기 자신이면 true를 반환한다. 이는 단순한 성능 최적화용으로, 비교 작업이 복잡한 상황일 때 값어치를 할 것이다.



#### 2. Instanceof 연산자로 입력이 올바른 타입인지 확인한다.



#### 3. 입력을 올바른 타입으로 형변환 한다.

앞서 2번에서 instanced 검사를 했기 때문에 이 단계는 100% 성공한다.



#### 4. 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사한다.

모든 필드가 일치하면 true를, 하나라도 다르면 false를 반환한다.



## Equals를 구현할 때 주의할 추가사항

#### 1. 기본타입과 참조타입

- 기본타입: float와 double을 제외한 기본 타입 필드는 == 연산자로 비교하고, float와 double 필드는 각각 정적 메서드인 Float.compare(float, float)와 Double.compare(double, double) 로 비교한다.
- 참조타입: equals() 비교
- 배열 필드는 원소 각각을 앞의 지침대로 비교한다.

#### 2. 필드 비교 순서는 equals 성능을 좌우한다.

- 다를 가능성이 높은 필드 우선
- 비교 비용이 싼 필드 우선

#### 3. equals를 재정의 할 땐 hashCode도 반드시 재정의하자.

#### 4. Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말자.

```java
//잘못된 예 - 입력타입은 반드시 Object여야 한다.
public boolean equals(MyClass o){
	...
}
```

이 메서드는 Objects.equals를 재정의한 게 아니다. 입력타입이 Object가 아니므로 재정의가 아니라 다중정의(아이템 52) 한 것이다.



### AutoValue 

Autovalue 프레임워크를 사용하면 equals에 대한 테스트를 용이하게 해준다.



















