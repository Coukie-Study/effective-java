# 정의하려는 것이 타입이라면 마커 인터페이스를 사용하라

아무 메서드도 담고 있지 않고, 단지 자신을 구현하는 클래스가 특정 속성을 가짐을 표시 해주는 인터페이스를 마커 인터페이스라고 한다.

Serializable 인터페이스가 좋은 예다.

```java
* possible, since such declarations apply only to the immediately declaring
 * class--serialVersionUID fields are not useful as inherited members. Array
 * classes cannot declare an explicit serialVersionUID, so they always have
 * the default computed value, but the requirement for matching
 * serialVersionUID values is waived for array classes.
 *
 * @author  unascribed
 * @see java.io.ObjectOutputStream
 * @see java.io.ObjectInputStream
 * @see java.io.ObjectOutput
 * @see java.io.ObjectInput
 * @see java.io.Externalizable
 * @since   JDK1.1
 */
public interface Serializable {
}
```



### 마커 인터페이스의 장점

- 마커 인터페이스는 이를 구현한 클래스의 인스턴스들을 구분하는 타입으로 쓸 수 있다.

  마커 인터페이스는 어엿한 타입이기 때문에, 마커 애너테이션을 사용했다면 런타임에야 발견될 오류를 컴파일 타임에 잡을 수 있다.

- 마커 인터페이스는 적용대상을 더 정밀하게 지정할 수 있다.

  - 적용대상을 @Target(Element.TYPE)으로 선언한 애너테이션은 모든타입(클래스, 인터페이스, 열거타입)에 달 수 있다.
  - 부착할 수 있는 타입을 더 세밀하게 제한하지 못한다.
  - 마커 인터페이스의 경우 마킹하고 싶은 클래스에만 그 인터페이스를 구현하면 돼서 자동으로 그 인터페이스의 하위 타입임이 보장된다.



### 마커 애너테이션의 장점

- 거대한 애너테이션 시스템의 지원을 받는다.
- 애너테이션을 적극 활용하는 프레임워크에서는 애너테이션을 사용하는게 일관성에 좋다.



### 정리

- 새로 추가하는 메서드 없이 단지 타입 정의가 목적이라면 마커 인터페이스를 선택하자.
- 클래스나 인터페이스 외의 프로그램 요소에 마킹해야 하거나, 애너테이션을 적극 활용하는 프레임워크에서 사용하려는 마커라면 마커 애너테이션을 사용하는것이 좋다.