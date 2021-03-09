# 가변인수는 신중히 사용하라

가변인수 메서드는 명시한 타입의 인수를 0개 이상 받을 수 있다. 가변인수 메서드를 호출하면, 가장 먼저 인수의 개수와 길이가 같은 배열을 만들고 인수들을 이 배열에 저장하여 가변인수 메서드에 건네준다.

```java
static int sum(int... args) {
        int sum = 0;
        for (int arg : args)
            sum += arg;
        return sum;
    }
```

sum(1,2,3)은 6을 sum()은 0을 리턴한다.

인수가 1개 이상이어야 할 때도 있다. 예를 들어 최솟값을 찾는 메서드인데 인수를 0개만 받을 수 있게 설계하면 안된다.

인수 개수는 런타임에 배열의 길이로 알 수 있다.

```java
static int min(int ...args){
    if(args.length == 0){
        throw new IllegalArgumentException("인수가 1개이상 필요합니다.");
    }
    int min=args[0];
    for(int i=1;i<args.length;i++){
        if(args[i]<min)
            min=args[i];
    }
    return min;
}
```

 이 경우 인수를 0개만 넣어 호출하면 런타임에 실패한다. 코드도 지저분하다.

더 깔끔한 방법이 있다.

```java
static int min(int firstArg, int... remainArgs) {
        int min = firstArg;
        for (int arg : remainArgs) {
            if (arg < min)
                min = arg;
        }
        return min;
    }
```

위 코드처럼 매개변수를 두개 받도록 하면 된다. 즉 첫번째로는 평범한 매개변수를 받고, 가변인수는 두 번째로 받으면 앞서의 문제가 말끔히 사라진다.



### 성능에 민감한 상황

성능에 민감한 상황이라면 가변인수가 걸림돌이 될 수 있다. 가변인수 메서든느 호출될 때마다 배열을 새로 하나 할당하고 초기화 한다. 다행히 가변인수의 유연성이 필요할 때 선택할 수 있는 패턴이 있다.

예를들어 해당 메서드 호출의 95%가 인수를 3개 이하로 사용한다고 해보자. 그렇다면 인수가 0개인것부터 4개까지 총 5개를 오버로딩하자. 그리고 마지막 다중정의 메서드가 인수4개 이상인 5%의 호출을 담당하는 것이다.

```java
public void foo() {}
public void foo(int a1) {}
public void foo(int a1,int a2) {}
public void foo(int a1,int a2, int a3) {}
public void foo(int a1,int a2, int a3, int ...rest) {}
```

따라서 메서드 호출 중 단 5%만이 배열을 생성한다.



### 정리

인수 개수가 일정하지 않은 메서드를 정의해야 한다면 가변인수가 반드시 필요하다. 메서드를 정의할 때 필수 매개변수는 가변인수 앞에두고, 가변인수를 사용할 때는 성능 문제도 고려하자.









