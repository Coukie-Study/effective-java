# 정확한 답이 필요하다면 float와 double을 사용하지 말자

- float와 double은 이진 부동소수점 연산에 쓰이며, 넓은 범위의 수를 빠르게 정밀한 근사치로 계산하도록 설계
- 근사치이기 때문에 정확한 계산이 불가능
- ex) `System.out.println(1.03 - 0.42)` = 0.6100000...?
- 반올림을 한다면 어느정도는 맞겠지만 틀리게 계산되는 경우도 존재

[부동소수점](https://ko.wikipedia.org/wiki/%EB%B6%80%EB%8F%99%EC%86%8C%EC%88%98%EC%A0%90)

```java
public static void main(String[] args) {
    double funds = 1.00;
    int itemsBought = 0;
    for (double price = 0.10; funds >= price; price += 0.10) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + " items bought.");
    System.out.println("Change: $" + funds);
}
```

- 위와 같이 코드를 짜면 잘못된 결과가 도출된다.
- 정확한 답을 원한다면 float, double을 사용하지 말고 BigDecimal, int, long을 사용하자

```java
public static void main(String[] args) {
    final BigDecimal TEN_CENTS = new BigDecimal(".10");

    int itemsBought = 0;
    BigDecimal funds = new BigDecimal("1.00"); // String
    for (BigDecimal price = TEN_CENTS;
         funds.compareTo(price) >= 0;
         price = price.add(TEN_CENTS)) {
        funds = funds.subtract(price);
        itemsBought++;
    }
    System.out.println(itemsBought + " items bought.");
    System.out.println("Money left over: $" + funds);
}
```

- 기본 타입보다 쓰기 불편하고, 느리다는 단점이 있지만, 정확한 계산을 할 수 있다.
- int, long을 쓰는 경우 필요한 소수점 계산을 직접 구현해야 한다. 혹은 정수 단위로 치환

```java
public static void main(String[] args) {
    int itemsBought = 0;
    int funds = 100;
    for (int price = 10; funds >= price; price += 10) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + " items bought.");
    System.out.println("Cash left over: " + funds + " cents");
}
```
