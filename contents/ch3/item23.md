# 아이템23. 태그 달린 클래스보다는 클래스 계층 구조를 활용하라.
### 태그 달린 클래스란?
두 가지 이상의 의미를 표현하며 현재 표현하는 의미를 태그 값으로 알려주는 클래스.

### 태그 달린 클래스 예시
````java
public class Figure {
    enum Shape { RECTANGLE, CIRCLE};

    // 태그 필드 - 현재 모양을 나타낸다.
    final Shape shape;

    // 다음 필드들은 모양이 사각형일 때만 쓰인다.
    double length;
    double width;

    // 다음 필드는 모양이 원일 때만 쓰안다.
    double radius;

    //원용 생성자
    Figure(double radius){
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    // 사각형용 생성자
    Figure(double length, double width){
        shape = Shape.RECTANGLE;
        this.length = length;
        this. width = width;
    }

    double area(){
        switch(shape){
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }

  public static void main(String[] args) {
    Figure f = new Figure(3.14);
    System.out.println(f.area());
  }
}
````

### 태그 달린 클래스 단점
- 열거 타입 선언, 태그 필드, switch문 등 쓸데없는 코드가 많다.
- 여러 구현이 한 클래스에 있어 가독성이 좋지 않다.
- 여러 구현으로 인한 메모리 낭비가 심하다.
- 쓰지 않는 final 필드를 초기화하는 불필요한 코드가 늘어난다.
- 의미를 추가하려면 코드 수정을 많이 해야한다.
- 인스턴스의 타입만으로는 현재 나타내는 의미를 알 수 없다.

태그 달린 클래스는 장황하고, 오류를 내기쉬우며, 비효율적이므로 클래스 계층구조를 활용하는게 좋다.

### 태그 달린 클래스를 클래스 계층구조로 바꾸는 방법
1. 루트가 될 추상 클래스를 정의하고, 태그 값에 따라 달라지는 메서드들(Figure 클래스의 area 메서드) 을 루트 클래스릐 추상 메서드로 선언하다.
2. 태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가한다.
3. 모든 하위 클래스에서 공통으로 사용하는 데이터 필드들도 전부 루트 클래스에 선언한다.
4. 루트 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다.
5. 각 하위 클래스에 각자의 의미에 해당하는 데이터 필드를 넣는다.
6. 루트 클래스가 정의한 추상 메서드를 각자 의미에 맞게 재정의 한다.

````java
abstract class Figure{
    abstract double area();
}

class Circle extends Figure{
    final double radius;
    
    Circle(double radius) {this. radius = radiues; }

    @Override double area() { return Math.PI * (radius * radius); }
}

class Rectangle extends Figure{
    final double length;
    final double width;
    
    Rectangle(double length, double width){
        this.length = length;
        this. width = width;
    }
    
    @Override double area() { return length * length; }
}
```` 
### 클래스 계층구조의 장점
1. 간결하고 명확하다.
2. 쓸데 없는 코드가 다 사라진다.
3. 관련없는 데이터필드가 사라진다.
4. 루트 클래스의 코드를 건드리지 않아도 된다.
5. 인스턴스의 의미를 명시할 수 있다.
6. 타입 사이의 자연스러운 계층 관계를 반영할 수 있다.
````java
class Square extends Rectangle{
    Square(double side){
        super(side, side);
    }
}
````
예를 들어 정사각형 클래스를 만들려면 Rectagle 클래스를 확장하며 단순한 변화를 주어 간단하게 만들 수 있다.