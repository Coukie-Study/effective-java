# 공개된 API 요소에는 항상 문서화 주석을 작성하라.
### 문서화 주석(Jacadoc) 이란?
Java 코드에서 API 문서를 HTML 형식으로 생성해주는 도구

### API를 올바로 문서화 하는 방법
- 모든 클래스, 인터페이스, 메서드, 필드 선언에서 문서화 주석을 단다.
    - 직렬화할 수 있는 클래스라면 직렬화 형태에 관해서도 적어야한다.
    - 기본 생성자에는 문서화 주석을 달 방법이 없으니 공개 클래스는 절대 기본 생성자를 사용하면 안된다.
    - 유지보수까지 고려하면 공개되지 않은 클래스, 필드 메서드 등에도 문서화 주석을 달아야 한다.
    
- 메서드용 문서화 주석에는 해당 메서드와 클라이언트 사이의 규약을 명료하게 기술해야 한다.
    - 메서드가 어떻게(how) 작동하는지가 아닌 무엇을(what) 하는지를 기술해야 한다.  
    - 전제조건, 사후조건, 부작용을 나열해야 한다.
    - 매개변수, 반환값, 예외조건으로 각각 @param, @return, @throws을 사용하여 기술한다.
    ````java
      /**
       * Returns the element at the specified position in this list.
       * 
       * <p>This method is <i>not</i> guaranteed to run in constant time</p>
       *
       * @param  index index of the element to return
       * @return the element at the specified position in this list
       * @throws IndexOutOfBoundsException if the index is out of range
       *         ({@code index < 0 || index >= this.size()})
       */
      public E get(int index) {
          rangeCheck(index);
          return elementData(index);
      }
    ````
    - 상속되는 메서드의 옳바른 재정의 방법을 명시하기 위해 @implSpec 주석을 사용한다.
    ````java
    /**
     * Returns true if this collection is empty
     *
     * @implSpec
     * This implementation returns {@code this.size() == 0}.
     *
     * @return true if this collection is empty
     */
    public boolen isEmpty() { ... }
    ```` 
  
- API 설명에 <, >, & 등의 HTML 메타문자를 포함시키려면 {@literal}태그로 감싸라
    ````java
    /**
     * A geometric series converges if |r| < 1
     * A geometric series converges if {@literal |r| < 1}
     * A geometric series converges if |r| {@literal <} 1
     */  
    ````
- 각 문서화 주석의 첫 번째 문장은 해당 요소의 요약 설명으로 간주된다.
    - 요약 설명은 반드시 대상의 기능을 고유하게 기술해야 한다.
    - 한 클래스 안에서 요약 설명이 똑같은 멤버가 둘 이상이면 안된다.
    - 문장의 판단을 마침표(.) 기준으로 하기에 첫 마침표를 주의해서 사용해야한다. 
    ````java
    /**
     * A suspect, such as Colonel Mustard or Mrs. Peacock.
     * A suspect, such as Colonel Mustard or {@literal Mrs. Peacock}.
     * {@summary A suspect, such as Colonel Mustard or Mrs. Peacock.}
     */
    ````
    - 메서드의 요약은 동작을 설명하는 동사구(~한다), 클래스, 인터페이스, 필드의 요약설명은 명사절(~하는 것)
    
- 제네릭, 열거 타입, 애너테이션의 문서화 주석은 특별히 주의한다.
    - 제네릭 타입이나 제네릭 메서드를 문서화할 때 모든 타입 매개변수에 주석을 달아야 한다.
    ````java
    /**
     *
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of mapped values
     * 
     */
    public class HashMap<K,V> extends AbstractMap<K,V>
    ````
    - 열거 타입을 문서화할 때는 상수들에도 주석을 달아야 한다.
    - 애너테이션 타입을 문서화할 때는 멤버들에도 모두 주석을 달아야 한다.
- 패키지를 설명하는 문서화 주석은 package-info.java 파일에 작성한다. 모듈 시스템을 사용한다면 module-info.java 파일에 작성한다.
- 클래스 혹은 정적 메서드가 스레드 안전하든 안전하지 않든 스레드 안전 수준을 API 설명에 포함해야한다.
- 직렬화 할 수 있는 클래스라면 직렬화의 형태를 API 설명에 기술 해야한다.
- 문서화 주석이 없는 API 요소를 발견하면 자바독이 가장 가까운 문서화 주석을 찾는다.(상위클래스, 상속한 인터페이스)
    - {@inheritDoc} 태그를 사용해 상위 타입의 문서화 주석 일부를 상속할 수 있다.
- 별도의 설명이 필요하고 그 설명에 관한 문서가 있을 경우 문서의 링크를 제공해 주면 좋다.
- 플러그인, command line의 옵션등을 추가해서 작성한 JavaDoc의 유효성 검사를 할 수 있다.
