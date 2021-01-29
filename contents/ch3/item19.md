# 19. 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라

### 재정의 가능한 메서드의 문서화

상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지(자기사용) 문서로 남겨야 한다. 클래스의 API로 공개된 메서드에서 클래스 자신의 또 다른 메서드를 호출할 수도 있다. 그런데 마침 호출되는 메서드가 재정의 가능 메서드라면 그 사실을 호출하는 메서드의 API 설명에 적시해야 한다.



```java
/**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the collection looking for the
     * specified element.  If it finds the element, it removes the element
     * from the collection using the iterator's remove method.
     *
     * <p>Note that this implementation throws an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by this
     * collection's iterator method does not implement the <tt>remove</tt>
     * method and this collection contains the specified object.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     */
    public boolean remove(Object o);
```

설명: 이 컬렉션이 주어진 객체를 갖고 있으나, 이 컬렉션의 iterator 메서드가 반환한 반복자가 remove 메서드를 구현하지 않았다면 UnsupportedOperationException을 던진다.

이 설명에 따르면 iterator 메서드를 재정의하면 remove 메서드의 동작에 영향을 줌을 확실히 알 수 있다.



### 클래스의 내부 동작 과정 중간에 끼어들 수 있는 훅(hook)을 잘 선별하여 protected 메서드 형태로 공개해야 할 수도 있다.

```java
/**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * <p>This method is called by the {@code clear} operation on this list
     * and its subLists.  Overriding this method to take advantage of
     * the internals of the list implementation can <i>substantially</i>
     * improve the performance of the {@code clear} operation on this list
     * and its subLists.
     *
     * <p>This implementation gets a list iterator positioned before
     * {@code fromIndex}, and repeatedly calls {@code ListIterator.next}
     * followed by {@code ListIterator.remove} until the entire range has
     * been removed.  <b>Note: if {@code ListIterator.remove} requires linear
     * time, this implementation requires quadratic time.</b>
     *
     * @param fromIndex index of first element to be removed
     * @param toIndex index after last element to be removed
     */
    protected void removeRange(int fromIndex, int toIndex) {
        ListIterator<E> it = listIterator(fromIndex);
        for (int i=0, n=toIndex-fromIndex; i<n; i++) {
            it.next();
            it.remove();
        }
    }
```

설명: fromIndex부터 toIndex까지의 모든 원소를 이 리스트에서 제거한다.

이 리스트 혹은 이 리스트의 부분리스트에 정의된 clear 연산이 이 메서드를 호출한다. 리스트 구현의 내부 구조를 활용하도록 이 메서드를 재정의 하면 이 리스트와 부분리스트의 clear 연산 성능을 크게 개선한다.

List 구현체의 최종 사용자는 removeRange 메서드에 관심이 없다. 그럼에도 이 메서드를 제공한 이유는 단지 하위 클래스에서 부분리스트의 clear 메서드를 고성능으로 만들기 쉽게 하기 위해서다. removeRange 메서드가 없다면 하위 클래스에서 clear 메서드를 호출하면 (제거할 원소 수의) 제곱에 비례해 성능이 느려진다.



### 그렇다면, 상속용 클래스에서 어떤 메소드를 protected로 노출해야 할지는 어떻게 결정할까?

상속용 클래스를 시험하는 방법은 직접 하위 클래스를 만들어 보는것이 '유일'하다. 놓친 protected 멤버는 검증 도중 빈자리가 확연히 드러날 것이고 반대로 여러 하위 클래스를 만들면서 전혀 쓰이지 않는 protected 멤버는 private이었어야 할 가능성이 크다.



### 상속용 클래스 설계 시, 주의사항

- 상속용 클래스의 생성자는 직접적으로든 간접적으로든 재정의 가능 메서드를 호출해서는 안된다.

```java
public class Super {
    public Super(){	//생성자가 재정의 가능 메서드를 호출한다.
        overrideMe();
    }
    public void overrideMe(){

    }
}
```

```java
public final class Sub extends Super{
    private final Instant instant;
    Sub(){
        instant=Instant.now();
    }
    @Override public void overrideMe(){
        System.out.println(instant);
    }

    public static void main(String[] args) {
        Sub sub=new Sub();	//Super의 생성자가 호출되고 그 과정에서 OverrideMe() 메서드가 호출됨.
        sub.overrideMe();
    }
}
```

![image-20210129043523823](/Users/suchoi53/Library/Application Support/typora-user-images/image-20210129043523823.png)



- Cloneable과 Serializable 인터페이스를 구현한 클래스는 상속할 수 없게하는것이 좋다.

  - clone과 readObject메서드는 생성자와 비슷한 효과를 낸다.(새로운 객체를 만든다) 따라서 clone과 readObject 모두 직접적으로든 간접적으로든 재정의 가능 메서드를 호출해서는 안된다.

  ### 정리

  상속용 클래스를 설계하려면 클래스 내부에서 스스로를 어떻게 사용하는지(자기사용 패턴) 모두 문서로 남겨야 하며, 일단 문서화 한것은 그 클래스까 쓰이는 한 반드시 지켜야 한다. 그렇지 않으면 그 내부 구현방식을 믿고 활용하던 하위 클래스를 오동작하게 만들 수 있습니다. 그리고 클래스를 확정해야 할 명확한 이유가 떠오르지 않으면 상속을 금지하는 편이 낫다. 상속을 금지하려면 클래스를 final로 선언하거나 생성자 모두를 외부에서 접근할 수 없도록 만들면 된다.

  

  

  



















