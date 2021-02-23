# ordinal 인덱싱 대신 EnumMap을 사용하라

```java
class Plant {
    enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

    final String name;
    final LifeCycle lifeCycle;

    Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override public String toString() {
        return name;
    }
}
```

- Enum을 활용하는 방법 중, ordinal을 활용해서 배열이나 리스트에서 원소를 꺼내오는 활용법이 있었다.

```java
Set<Plant>[] plantsByLifeCycleArr = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
for (int i = 0; i < plantsByLifeCycleArr.length; i++)
    plantsByLifeCycleArr[i] = new HashSet<>();
for (Plant p : garden)
    plantsByLifeCycleArr[p.lifeCycle.ordinal()].add(p);
// Print the results
for (int i = 0; i < plantsByLifeCycleArr.length; i++) {
    System.out.printf("%s: %s%n",
            Plant.LifeCycle.values()[i], plantsByLifeCycleArr[i]);
}
```

- 위와 같은 방법으로 Enum을 활용할 경우, 우선 배열은 제네릭과 호환되지 않아 비검사 형변환 경고를 보낼 것이고, 각 인덱스 별 의미를 설명하기 위해 레이블도 필요
- 가장 큰 문제는 정확한 정숫값을 사용한다는 걸 사용자가 검증해야 한다는 것

## EnumMap

- 열거 타입을 키로 사용하도록 설계한 Map 구현체

```java
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);
for (Plant.LifeCycle lc : Plant.LifeCycle.values())
    plantsByLifeCycle.put(lc, new HashSet<>());
for (Plant p : garden)
    plantsByLifeCycle.get(p.lifeCycle).add(p);
System.out.println(plantsByLifeCycle);
```

- EnumMap은 내부적으로 배열을 사용해 타입 안정성을 얻으면서도 성능을 향상

```java
public V get(Object key) {
   return (isValidKey(key) ? unmaskNull(vals[((Enum<?>)key).ordinal()]) : null);
}
```

- EnumMap 생성 시 Class 객체를 받는데 이는 런타임 제네릭 타입 정보를 제공한다.
- Stream API를 사용하면 더 간단히 맵을 만들 수 있다.

```java
System.out.println(Arrays.stream(garden)
                .collect(groupingBy(p -> p.lifeCycle)));
```

- 다만 groupby로 직접 맵을 만들면 구현체가 EnumMap이 아니라 다른 구현체가 반환된다. 따라서 mapFactory 매개변수에 직접 원하는 구현체를 넣어줄 수 있다.

```java
System.out.println(Arrays.stream(garden)
                .collect(groupingBy(p -> p.lifeCycle,
                        () -> new EnumMap<>(LifeCycle.class), toSet())));
```

- 두 열거 타입 값을 매핑하기 위해 ordinal을 2번 쓴 배열 of 배열을 쓰는 경우도 있다.

```java
public enum Transition {
    MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

    private static final Transition[][] TRANSITIONS = {
        { null, MELT, SUBLIME },
        { FREEZE, null, BOIL },
        { DEPOSIT, CONDENSE, null }
    };
}
```

- 이렇게 사용할 경우 여전히 컴파일러는 ordinal과 배열 인덱스의 관계를 알 수 없으며, Enum을 수정하면서 배열 of 배열을 수정하지 않을 경우 오류가 발생할 수 있다.
- 차라리 EnumMap을 중첩해서 사용하자

```java
public enum Phase {
    SOLID, LIQUID, GAS;
    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase from;
        private final Phase to;
        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // Initialize the phase transition map
        private static final Map<Phase, Map<Phase, Transition>>
                m = Stream.of(values()).collect(groupingBy(t -> t.from,
                () -> new EnumMap<>(Phase.class),
                toMap(t -> t.to, t -> t,
                        (x, y) -> y, () -> new EnumMap<>(Phase.class))));
        
        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}
```

- 새로운 상태를 추가해도 기존과 달리 깔끔하게 코드 작성이 가능하다.

```java
SOLID, LIQUID, GAS, PLASMA;
public enum Transition {
    MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
    BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
    SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
    IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);
```
