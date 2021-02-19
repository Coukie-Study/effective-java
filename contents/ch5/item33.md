# 타입 안전 이종 컨테이너를 고려하라.
### 타입 안전 이종 컨테이너란?
Set< E >, Map< K, V > 와 같은 일반적인 컨테이너는 자기 자신이 매개변수화 타입(제네릭 타입)이므로<br>
매개변수화 할 수 있는 타입의 수가 제한된다. 하지만 데이터베이스를 Map구조로 구현하려면<br>
데이터베이스의 컬럼은 다양한 타입이 될 수 있기에 Map의 Key또한 다양한 타입이 될 수 있어야한다<br>
이때 Map 자체를 제네릭 타입으로 만드는대신 Map의 Key를 제네릭 타입으로 선언해 해결 할 수 있는데<br>
이러한 컨테이너 패턴을 타입 안전 이종 컨테이너 패턴이라고 한다.

### Class< T > 클래스
Class 클래스는 제네릭 타입으로 클래스의 구조(메서드, 필드, 클래스의 이름)를 객체화 한 클래스다.<br>
흔히 사용하는 String.class, Integer.class 의 타입이 Class< String >, Class< Integer >에 해당한다.

### 타입 안전 이종 컨테이너 예시
````java
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();
    
    public <T> void putFavorite(Class<T> type, T instance){
        favorites.put(Objects.requireNonNull(type), instance);
    }
    
    public <T> T getFavorite(Class<T> type){
        return type.cast(favorites.get(type));
    }
}

public static void main(String[] args){
    Favorites f = new Favorites();
    
    f.putFavorite(String.class, "Java");
    f.putFavorite(Integer.class, 0xcafebebe);
    f.putFavorite(Class.class, Favorties.class);
    
    String favoriteString = f.getFavorite(String.class);
    int favoriteInteger = f.getFavorite(Integer.class);
    Class<?> favoriteClass = f.getFavorite(Class.class);
    
    //Java cafebabe Favorites
    System.out.println("%s %x %s%n", favoriteString,
        favoriteInteger,favoriteClass.getName());
}   
````
- Map<Class< ? >, Object>은 Map의 key에 와일드 카드 타입을 선언했으므로 <br>
key값으로 Class< String >, Class< Integer > 등의 값을 넣을 수 있다.
- Map<Class< ? >, Object>의 value는 Object 이므로 key와 value 사이에 타입 관계를 보증하지 않는다.<br>
그럼에도 이렇게 선언한것은 이 관계를 명시할 방법이 없기 때문이다.
- putFavorite 구현에서 Class의 type 메서드가 쓰였기에 다시 key와 value 사이에 타입 관계를 되살릴 수 있다.

````java
public class Class<T>{
    public T cast(Object obj) {
            if (obj != null && !isInstance(obj))
                 throw new ClassCastException(cannotCastMsg(obj));
            return (T) obj;
    }
}
````
### Favorites 클래스의 제약
- 악의적인 클라이언트가 Class객체를 (제네릭이 아닌) 로타입 으로 넘기면 Favorites 인스턴스의 타입 안전성이 쉽게 깨진다.<br>
````java
f.putFavorite((Class)Integer.class, "Integer의 인스턴스가 아닙니다.");
int favoriteInteger = f.getFavorite(Integer.class);
```` 
이를 방지하기 위한 방법
````java
public <T> void putFavorite(Class<T> type, T instance){
        favorites.put(Objects.requireNonNull(type), type.casst(instance));
}
````

- 실체화 불가 타입에는 사용할 수없다.

List는 List.class라는 같은 Class를 공유하기 때문에 List< String >.class, List< Integer >.class 는 문법 오류가 난다.<br>
그러므로 Map< Class< ? >, Object >인 favorites에 key로 등록할 수없다.

###허용하는 타입을 제한하고 싶은 경우
- 한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입을 제안할 수 있다.

````java
//AnnotatedElement 인터페이스의 메서드
public <T extends Annotation>
    T getAnnotation(Class<T> annotationType);
````
#### asSubclass를 사용한 타입 제한
Class< ? >타입의 객체가 있고 이를 getAnnotation같은 한정적 타입 토큰을 받는 메서드에 넘기는경우,<br>
Class< ? > 대신 Class< ? extends Annotation > 으로 형변환하여 넘길 수 있지만 이 형변환은 비 검사 이므로 컴파일시 경고가 뜬다.<br>
Class 클래스에는 이런 형변환을 안전하게 동적으로 수행해주는 인스턴스 메서드 "asSubclass" 를 제공한다.
````java
//결과 : class java.lang.Integer
System.out.println(Integer.class.asSubclass(Number.class));

//결과 : Exception in thread "main" java.lang.ClassCastException
System.out.println(Integer.class.asSubclass(String.class));
````
#### 예시
````java
static Annotation getAnnotation(AnnotatedElement element, 
        String annotationTypeName){
    Class<?> annotationType = null; // 비한정적 타입 토큰
    try{
        annotationType = Class.forName(annotationTypeName);
    }catch (Exception ex){
        throw new IllegalArgumentException(ex);
    }
    return element.getAnnotation(
        annotationType.asSubclass(Annotation.class));
}
````
