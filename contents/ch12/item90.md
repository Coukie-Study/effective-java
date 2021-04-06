# 직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라
### 직렬화 프록시란?
바깥 클래스(직렬화 대상인 클래스)의 논리적 상태를 동일하게 표현한 중첩클래스를 private static으로 선언한다.
<br> 이 클래스가 직렬화 프록시다. 중첩클래스의 생성자는 단 하나여야 하며 바깥 클래스를 매개변수로 받는다.
<br> 바깥 클래스와 직렬화 프록시 모두 Serializable을 구현한다고 선언해야한다.
<br> 직렬화 프록시의 목적은 이전 장에서 설명해온 Serializable을 선언하였을때 생기는 문제점들을
<br> 직렬화 대상 클래스를 직접 직렬화 하는것이 아닌 동일한 데이터값을 갖는 클래스를 직렬화하여 문제점을 해결하는 것이다.
````java
class Period implements Serializable { 
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 2123123123;
        private final Date start;
        private final Date end;

        public SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        /**
         * Deserialize 할 때 호출된다.
         * 오브젝트를 생성한다.
         */
        private Object readResolve() {
            return new Period(start, end);
        }
    }


    /**
     * 이로 인해 바깥 클래스의 직렬화된 인스턴스를 생성할 수 없다.
     * 직렬화할 때 호출되는데, 프록시를 반환하게 하고 있다.
     *
     * Serialize할 때 호출된다.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * readObject, writeObject 가 있다면, 기본적으로 Serialization 과정에서
     * ObjectInputStream, ObjectOutputStream이 호출하게 된다.
     * 그 안에 커스텀 로직을 넣어도 된다는 것.
     */
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        // readObject는 deserialize할 때, 그러니까 오브젝트를 만들 때인데.
        // 이렇게 해두면, 직접 Period로 역직렬화를 할 수 없는 것이다.
        throw new InvalidObjectException("프록시가 필요해요.");
    }
}
````
직렬화 프록시 패턴 장점
- 역직렬화 과정에서 생성자를 통해 인스턴스를 반환하므로 생성자에서만 불변식을 헤치지 않는지 고려하면 된다.
- 앞선 장에서 설명한 모든 공격들(내부 필드 탈취 등)을 모두 방지할 수 있다.
- Period의 필드를 final로 선언하여 불변으로 만들 수 있다. 
- 역직렬화시 유혀성을 검사할 필요가 없다.
- 직렬화 프록시 패턴은 역직렬화한 인스턴스와 원래의 직렬화된 인스턴스의 클래스가 달라도 정상 작동한다.

직렬화 프록시 패턴 한계
- 클라이언트가 멋대로 확장할 수 있는 클래스에는 적용할 수 없다.
- 객체 그래프에 순환이 있는 클래스에도 적용할 수 없다.