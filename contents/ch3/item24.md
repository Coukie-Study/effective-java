# 아이템24. 멤버 클래스는 되도록 static으로 만들라.
### 중첩 클래스란?
중첩 클래스란 다른 클래스 안에 정의된 클래스를 말한다. 중첩 클래스는 자신을 감싼<br>
바깥 클래스에서만 쓰여야 하며, 그 외의 쓰임세가 있으면 톱레벨 클래스로 만들어야 한다.<br>

#### 중첩 클래스의 종류
- 정적 멤버 클래스
- 비정적 멤버 클래스
- 익명 클래스
- 지역 클래스

정적 멤버 클래스를 제외한 3가지는 내부 클래스(inner class)이다. 

#### 정적 멤버 클래스
- 다른 클래스 안에 선언되고, 바깥 클래스의 private 맴버에도 접근할 수 있다는 점을 제외하면<br>
일반적인 클래스와 동일하다. 
- 다른 정적 멤버와 똑같은 접근 규칙을 적용받는다. 예를 들어 private으로 선언하면 바깥 클래스에서만 접근 가능하다.
- 정적 멤버 클래스는 바깥 클래스와 함께 쓰일 때만 유용한 public 도우미 클래스로 쓰인다.
````java
public class Test{
  public static void main(String[] args) {
    int a = 10;
    int b = 20;
    System.out.println(Calculator.Operation.add(a, b));
    System.out.println(Calculator.Operation.sub(a, b));
    // result : 30, -10
  }
}

class Calculator {
    public static class Operation{
        public static int add(int a, int b){
            return a + b;
        }
        public static int sub(int a, int b){
            return a - b;
        }
    }
}
````

#### 비정적 멤버 클래스

- 비정적 멤버 클래스의 인스턴스는 바깥 클래스의 인스턴스와 암묵적으로 연결된다.
- 정규화된 this(클래스명.this)를 사용해 바깥 인스턴스의 메서드를 호출하거나 인스턴스 참조가 가능하다.
- 개념상 중첩 클래스의 인스턴스가 바깥 인스턴스와 독립적으로 존재할 수 있다면 정적 멤버 클래스로 만들어야 한다.
- 비정적 멤버 클래스는 바깥 인스턴스 없이 생성할 수 없다.
- 비정적 멤버 클래스 인스턴스와 바깥 클래스 인스턴스와의 관계는 바깥 클래스의 인스턴스로 비정적 멤버 인스턴스의 생성자를 호출할 때 자동으로 만들어진다.
- 드물게 바깥 인스턴스의 클레스.new MemberClass(args)로 호출해 수동으로 만들기도 하지만 관계정보가 비정적 멤버 클래스의 인스턴스 안에 만들어져 <br>
메모리 공간을 차지하고 시간이 오래 걸린다.

````java
public class Test {
  public static void main(String[] args) {
    A a = new A();
    // B의 인스턴스는 A의 인스턴스로 생성해야 한다.
    A.B b = a.new B();

    // result : 10
    a.num = 10;
    b.print();

    // result : 20
    a.num = 20;
    b.print();
  }
}

class A {
  int num;

  class B {
    public void print() {
      //정규화된 this
      System.out.println(A.this.num);
    }
  }
}
````

- 어댑터를 정의할 때 자주 쓰인다.
````java
public class MySet<E> extends AbstractSet<E>{
    @Override public Iterator<E> iterator(){
        return new MyIterator();
    }
    
    private class MyIterator implements Iterator<E>{
        ... 
    }
}
````
#### 맴버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙혀 정적 멤버 클래스로 만들자.
- 비정적 멤버 클래스는 바깥 인스턴스로부터 숨은 외부 참조를 갖게 되므로
외부 참조를 저장하기위해 시간과 공간이 소비된다. 
- 가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못하는 메모리 누수가 생길 수 있다.

#### private 정적 멤버 클래스는 바깥 객체의 구성요소를 나타낼 때 쓰인다.
바깥 클래스와 독립적이며 바깥 객체의 구성요소로 사용될때 private 정적 멤버 클래스를 사용한다.

#### 익명 클래스
- 익명 클래스는 이름이 없다.
- 바깥 클래스의 멤버가 아니다.
- 멤버와 달리, 쓰이는 시점에 선언과 동시에 인스턴스가 만들어진다.
- 코드의 어디서든 만들 수 있다.
- 비 정적인 문맥에서 사용될 때만 바깥 클래스의 인스턴스를 참조할 수 있다.
- 정적 문맥에서라도 상수 변수 이외이 정적 맴버를 가질 수 없다. 즉 final 기본타입과, 문자열 필드만 가질 수 있다.
- instanceof 검사나 클래스 이름이 필요한 작업은 수행할 수 없다.
- 람다를 지원하기 전 즉석에서 작은 함수 객체나 처리 객체를 만드는 데 사용 되었다.

````java
 Thread a = new Thread(){
    String str = "hello";
    final int num = 10;
    public void run(){
        System.out.println(str + num);
    }
};
````
#### 지역 클래스 
- 네 가지 중첩 클래스 중 가장 드물게 사용된다. 
- 지역변수를 선언할 수 있는 곳이면 어디든 선언할 수 있다.
- 유효 범위 또한 지역 변수와 같다.
- 멤버 클래스 처럼 이름이 있고 반복해서 사용할 수 있다.
- 비정적 문맥에서 사용될 때만 바깥 인스턴스를 참조할 수 있다.
- 정적 멤버는 가질 수 없다.
- 가독성을 위해 짧게 작성되어야 한다. 
````java
class Test {
    public void print() {
        class LocalClass { 
            public void sayHello() {
                System.out.println("Hello");
            }
        }
        LocalClass lc = new LocalClass();
        lc.sayHello();
    }
}
````