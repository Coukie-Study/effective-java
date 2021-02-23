# 명명 패턴보다 애너테이션을 사용하라

## 명명 패턴

- 기존에는 도구나 프레임워크가 특별히 다뤄야 할 프로그램 요소를 구분할 명명 패턴을 사용
- ex) junit3에서는 test로만 테스트 메소드를 작성 `testSafety`

### 단점

- 명명 패턴에서 오타가 발견되면 안된다. ex) `tsetSafety`는 테스트가 되지 않는다.
- 올바른 프로그램 요소에서만 사용되는 보장이 없다.
    - 메소드에만 정의되는 명명 패턴을 클래스에 적용한다고 하더라도 제대로 동작하지 않는다.
- 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다.
    - 예외를 테스트 한다고 가정할 때, `XXX.class`를 직접 전달할 방법이 없다.

## Annotation

- JEE5부터 도입된 문법으로 프로그램 소스,코드 안에 다른 프로그램을 위한 정보를 미리 약속된 형식으로 포함시키기 위해 도입되었다. JUnit도 4부터 도입

### Marker annotation

- ex) 자동으로 수행되는 간단한 Test용 애너테이션

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
```

- 위와 같이 아무런 매개변수 없이 단순히 대상을 마킹하는 애너테이션을 마커 애너테이션이라 한다.
- 애너테이션에 또 다른 애너테이션이 달려있는 것을 확인할 수 있는데 이는 메타 애너테이션이라고 한다.
    - @Retention은 애너테이션이 언제까지 유지되어야 하는 지를 나타내는 것으로 이 경우 RUNTIME 시까지 애너테이션 정보가 유지된다는 뜻이다.
    - @Target은 애너테이션이 어느 위치에서 사용되는지를 알려주며 이 경우 메소드에서 사용된다는 것을 알려준다.
- `매개 변수 없는 정적 메서드 전용이다`는 제약을 직접 애너테이션을 이용해 강제하거나 애너테이션을 이용해 새로운 코드를 만들고 싶다면 애너테이션 처리기를 직접 구현해야 한다. `javax.annotation.processing` API를 참고

- 위 애너테이션은 단순히 마커의 역할만 할 뿐 실질적으로 기능을 수행하지는 않는다. 다만 이 애너테이션을 이용하는 도구에서 처리를 할 수 있도록 도와주는 역할을 한다.
- 사용 예 : 클래스 이름을 받아 테스트를 호출하는 메소드

```java
public class RunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " failed: " + exc);
                } catch (Exception exc) {
                    System.out.println("Invalid @Test: " + m);
                }
            }
        }
        System.out.printf("Passed: %d, Failed: %d%n",
                passed, tests - passed);
    }
}
```

### 매개 변수를 받는 애너테이션 타입

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```

- 특정 예외를 던지면 성공하는 테스트 만들고 싶다면 위와 같이 Throwable를 확장한 클래스를 매개 변수로 받는 애너테이션을 사용할 수 있다.

```java
public class RunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " failed: " + exc);
                } catch (Exception exc) {
                    System.out.println("Invalid @Test: " + m);
                }
            }

            if (m.isAnnotationPresent(ExceptionTest.class)) {
                tests++;
                try {
                    m.invoke(null);
                    System.out.printf("Test %s failed: no exception%n", m);
                } catch (InvocationTargetException wrappedEx) {
                    Throwable exc = wrappedEx.getCause();
                    Class<? extends Throwable> excType =
                            m.getAnnotation(ExceptionTest.class).value();
                    if (excType.isInstance(exc)) {
                        passed++;
                    } else {
                        System.out.printf(
                                "Test %s failed: expected %s, got %s%n",
                                m, excType.getName(), exc);
                    }
                } catch (Exception exc) {
                    System.out.println("Invalid @ExceptionTest: " + m);
                }
            }
        }

        System.out.printf("Passed: %d, Failed: %d%n",
                passed, tests - passed);
    }
}
```

- `InvocationTargetException`는 리플렉션을 이용해서 메서드를 실행할 때 기존에 던지는 예외를 한번 감싸는 역할을 한다. 따라서 `getCause()`를 통해 기존 예외를 가지고 올 수 있다.
- 컴파일 단계에서 매개 변수가 유효한 예외인지를 확인 가능하다. 다만 런타임에 예외 클래스가 존재하지 않을 수가 있는데, 이 경우 `TypeNotPresentException`을 던진다.

### 배열 매개변수를 받는 애너테이션 타입

- 단일 원소배열을 받을 수 있을 뿐만 아니라 앞의 예처럼 하나의 인자만 받을 수도 있다.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Exception>[] value();
}
```

```java
if (m.isAnnotationPresent(ExceptionTest.class)) {
    tests++;
    try {
        m.invoke(null);
        System.out.printf("Test %s failed: no exception%n", m);
    } catch (Throwable wrappedExc) {
        Throwable exc = wrappedExc.getCause();
        int oldPassed = passed;
        Class<? extends Throwable>[] excTypes =
                m.getAnnotation(ExceptionTest.class).value();
        for (Class<? extends Throwable> excType : excTypes) {
            if (excType.isInstance(exc)) {
                passed++;
                break;
            }
        }
        if (passed == oldPassed)
            System.out.printf("Test %s failed: %s %n", m, exc);
    }
```

### 반복 가능 애너테이션

- 배열로 매개 변수를 받는 예 말고도 @Repeatable 메타 애너테이션을 통해 여러 값을 받을 수 있다.
- 다만 주의할 점이 있는데
    - @Repeatable 애너테이션을 반환하는 컨테이너 애너테이션을 하나 더 정의하고, @Repeatable에 이 컨테이너 애너테이션의 class 객체를 매개변수로 전달해야 한다.
    - 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 한다.
    - 컨테이너 애너테이션에도 @Retention과 @Target을 명시해야  한다.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value();
}
```

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```

- isAnnotationPresent를 쓸 때 애너테이션이 반복되는 숫자에 따라 container 애너테이션을 넣어주어야 할 때도 있고 기존 애너테이션을 넣어주어야 할 때가 있다. (하나만 있으면 기존 애너테이션을 넣어야 true, 여러 개라면 container 애너테이션을 넣어야 true)

```java
if (m.isAnnotationPresent(ExceptionTest.class)
        || m.isAnnotationPresent(ExceptionTestContainer.class)) {
    tests++;
    try {
        m.invoke(null);
        System.out.printf("Test %s failed: no exception%n", m);
    } catch (Throwable wrappedExc) {
        Throwable exc = wrappedExc.getCause();
        int oldPassed = passed;
        ExceptionTest[] excTests =
                m.getAnnotationsByType(ExceptionTest.class);
        for (ExceptionTest excTest : excTests) {
            if (excTest.value().isInstance(exc)) {
                passed++;
                break;
            }
        }
        if (passed == oldPassed)
            System.out.printf("Test %s failed: %s %n", m, exc);
    }
}
```

- 정리
    - 애너테이션으로 처리할 수 있다면 굳이 명명 패턴을 쓰지 말자
    - 자바 프로그래머라면 예외 없이 자바가 제공하는 애너테이션 타입들은 사용해야 한다.
