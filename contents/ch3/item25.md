# 아이템25. 톱레벨 클래스는 한 파일에 하나만 담으라.

소스 파일 하나에 톱레벨 클래스를 여러개 선언해도 컴파일시 오류는 나지 않는다.<br>
하지만 어느 소스 파일을 먼저 컴파일 하냐에 따라 결과가 달라질 수 있다.

````java
//Main.java
public class Main{
	public static void main(String[] args){
		System.out.println(Utensil.NAME + Dessert.NAME);
	}
}
````

````java
//Utensil.java
class Utensil{
	static final String NAME = "pan";
}
class Dessert{
	static final String NAME = "cake";
}
````

````java
//Dessert.java
class Utensil{
        static final String NAME = "pot";
}
class Dessert{
        static final String NAME = "pie";
}
````
### 컴파일 순서에 따른 결과
#### javac Main.java Dessert.java 
1. Main.java 를 컴파일 한다.
2. 그 안에 Utensil 참조를 만난다.
3. Utensil.java 파일을 살펴 Utensil과 Dessert 정의를 찾는다.
4. Dessert.java를 컴파일 하려는 과정에서 이미 정의된 Utensil, Dessert로인해 오류가 난다.

#### javac Main.java
위 과정에서 4번을 제외한 것이므로 오류가 나지않고 컴파일이 된다.<br>
"pancake" 을 결과로 출력한다.

#### javac Main.java Utensil.java
Main에서 이미 Utensil 참조를 만나 Javac Main.java 만 한 결과와 일치한다.

#### javac Dessert.java Main.java
1. Dessert.java 를 컴파일 한다.
2. 그 안에 Dessert, Utensil 클래스를 정의한다.
3. Main.java 를 컴파일한다.
4. Utensil, Dessert 참조를 만나도 이미 Dessert.java를 컴파일하는 과정에서 정의가 되어있다.

결과로 "potpie"를 출력한다.<br><br>

이처럼 소스파일 컴파일 순서에 따라 결과가 바뀔수도, 오류가 날 수 있으므로 한 소스파일에 톱테벨 클래스를 여러개 담으면 안된다.

### 해결책
단순히 톱레벨 클래스를 분리하면 된다.<br>
만약 여러 톱레벨 클래스를 한 파일에 담고 싶으면 정적 맴버 클래스를 사용하는 방법을 고려하는게 좋다.
````java
public class Test{
    public static void main(String[] args){
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
    
    private static class Utensil{
        static final String NAME = "pan";
    }
    
    private static class Dessert{
        static final String NAME = "cake";
    }
}
````

````java

````

