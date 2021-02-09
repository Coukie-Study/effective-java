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

