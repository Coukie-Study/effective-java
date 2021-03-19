# 리플렉션보다는 인터페이스를 사용하라

### 리플렉션

- 리플렉션 기능을 이용하면 프로그램에서 임의의 클래스에 접근할 수 있다.
- Class 객체가 주어지면 그 클래스의 생성자, 메서드, 필드에 해당하는 인스턴스를 가져올 수 있고 이 인스턴스들로 그 클래스의 멤버 이름, 필드타입, 메서드 시그니처 등을 가져올 수 있다.
- 또한 이 인스턴스들을 통해 해당 클래스의 인스턴스를 생성하거나, 메서드를 호출하거나, 필드에 접근할 수 있다.



### 리플렉션의 단점

리플렉션을 이용하면 컴파일 당시에 존재하지 않던 클래스도 이용할 수 있는데, 몇 가지 단점이 있다.

- 컴파일타임 타입 검사가 주는 이점을 하나도 누릴 수 없다.
  - 프로그램이 리플렉션 기능을 써서 존재하지 않는 혹은 접근할 수 없는 메서드를 호출하려 시도하면 런타임 오류가 발생한다.
- 리플렉션을 이용하면 코드가 지저분하고 장황해진다.
- 성능이 떨어진다.



### 리플렉션은 아주 제한된 형태로만 사용해라

- 리플렉션은 아주 제한된 형태로만 사용해야 그 단점을 피하고 이점만 취할 수 있다.
- 컴파일타임에 이용할 수 없는 클래스를 사용해야만 하는 프로그램은 비록 컴파일타임이라도 적절한 인터페이스나 상위클래스를 이용할 수는 있을 것이다.
- 이런 경우라면 리플렉션은 인스턴스 생성에만 쓰고 이렇게 만든 인스턴스는 인터페이스나 상위 클래스로 참조해 사용하자.

```java
//리플렉션으로 생성하고 인터페이스로 참조해 활용
public static void main(String[] args) {
        Class<? extends Set<String>> cl = null;
        try {
            cl = (Class<? extends Set<String>>) Class.forName(args[0]);
        } catch (ClassNotFoundException e) {
            fatalError("클래스를 찾을 수 없습니다.");
        }

        Constructor<? extends Set<String>> cons = null;
        try {
            cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fatalError("매개변수 없는 생성자를 찾을 수 없다.");
        }

        Set<String> s = null;
        try {
            s = cons.newInstance();
        } catch (IllegalAccessException e) {
            fatalError("생성자에 접근할 수 없다.");
        } catch (InstantiationException e) {
            fatalError("클래스를 인스턴스화 할 수 없다.");
        } catch (InvocationTargetException e) {
            fatalError("생성자가 예외를 던졌다." + e.getCause());
        } catch (ClassCastException e){
            fatalError("Set을 구현하지 않은 클래스이다.");
        }
        s.addAll(Arrays.asList(args).subList(1, args.length));
        System.out.println(s);
    }

    private static void fatalError(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
```

- 런타임에 총 여섯가지나 되는 예외를 던질 수 있다.
  - 리플렉션 없이 생성했다면 컴파일 타임에 잡아 낼 수 있었을 예외들이다.
- 클래스 이름만으로 인스턴스를 생성해내기 위해 무려 25줄이나 되는 코드를 작성했다.



### 정리

- 리플렉션은 복잡한 특수 시스템을 개발할 때 필요한 강력한 기능이지만 단점도 많다.
- 컴파일 타임에는 알 수 없는 클래스를 사용하는 프로그램을 작성한다면 리플렉션을 사용해야 할 것이다.
- 되도록 객체생성에만 사용하고, 생성한 객체를 이요할 때는 적절한 인터페이스나 컴파일 타임에 알 수 있는 상위 클래스로 형변환해 사용해야 한다.

