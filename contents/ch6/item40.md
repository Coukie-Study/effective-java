# @Override 애너테이션을 일관되게 사용하라

자바가 기본으로 제공하는 애너테이션 중 보통의 프로그래머에게 가장 중요한 것은 @Override일 것이다.

@Override는 메서드 선언에만 달 수 있으며, 이 애너테이션의 의미는 상위 클래스의 메서드를 재정의 했음을 의미한다.

```java
//영어 알파벳 2개로 구성된 문자열을 표현하는 클래스
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size());
    }
}
```

Bigram에 a~z까지 26개의 문자를 넣고, 10개씩 만든 다음에 HashSet에 삽입했다.

Set은 중복을 허용하지 않으므로 26개가 나올 것 같지만, 실제로는 260개가 발생한다.

Object의 equals를 재정의 하려면 매개변수 타입을 Object로 해야만 하는데 그렇게 하지 않아서 Overriding을 한 것이 아니라 Overloading 해버렸다.

따라서 같은 소문자를 소유한 Bigram 10개 각각이 서로 다른 객체로 인식되고, 결국 260을 출력한 것이다.



```java
@Override
    public boolean equals(Object o) {
        if(!(o instanceof Bigram))
            return false;
        Bigram b = (Bigram) o;
        return b.first == first && b.second == second;
    }
```

상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자.



### @Override를 안달아도 되는 예외 케이스

구체 클래스에서 상위클래스의 추상 메서드를 재정의할 때는 굳이 @Override를 달지 않아도 된다. 구체 클래스인데 아직 구현하지 않은 추상 메서드가 남아 있다면 컴파일러가 그 사실을 바로 알려주기 때문이다.



