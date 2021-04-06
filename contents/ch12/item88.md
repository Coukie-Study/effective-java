# readObject 메서드는 방어적으로 작성하라.
### 방어적 복사를 사용하는 불변 클래스
````java
public final class Period {
    private final Date start;
    private final Date end;

    /**
     * @param  start 시작 시각
     * @param  end 종료 시각; 시작 시각보다 뒤여야 한다.
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
     * @throws NullPointerException start나 end가 null이면 발생한다.
     */
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime()); // 가변인 Date 클래스의 위험을 막기 위해 새로운 객체로 방어적 복사
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + " after " + end);
        }
    }

    public Date start() { return new Date(start.getTime()); }
    public Date end() { return new Date(end.getTime()); }
    public String toString() { return start + " - " + end; }
    // ... 나머지 코드는 생략
}
````
### readObject는 실질적으로 또 다른 public 생성자이다.
- readObject는 매개변수로 바이트 스트림을 받는 생성자로 생각해야 한다.
- readObject 또한 다른 생성자와 똑같은 수준으로 주의를 기울여야 한다.
- 보통의 생성자처럼 인수가 유효한지 검사하 필요하다면 매개변수를 방어적으로 복사해야한다.고

### 단순히 implements Serializable 을 통해 직렬화를 한경우(readObject에 주의를 기울이지 않은 경우)
````java
public class BogusPeriod {
    // 진짜 Period 인스턴스에서는 만들어질 수 없는 바이트 스트림,
    // 정상적인 Period 인스턴스를 직렬화한 후에 손수 수정한 바이트 스트림이다.
    private static final byte[] serializedForm = {
        (byte)0xac, (byte)0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x06,
        0x50, 0x65, 0x72, 0x69, 0x6f, 0x64, 0x40, 0x7e, (byte)0xf8,
        ... 생략
    }

    // 상위 비트가 1인 바이트 값들은 byte로 형변환 했는데,
    // 이유는 자바가 바이트 리터럴을 지원하지 않고 byte 타입은 부호가 있는(signed) 타입이기 때문이다.

    public static void main(String[] args) {
        Period p = (Period) deserialize(serializedForm);
        System.out.println(p);
    }

    // 주어진 직렬화 형태(바이트 스트림)로부터 객체를 만들어 반환한다.
    static Object deserialize(byte[] sf) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sf)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                return objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

# 실행 결과, end가 start 보다 과거다. 즉, Period의 불변식이 깨진다.
Fri Jan 01 12:00:00 PST 1999 - Sun Jan 01 12:00:00 PST 1984
````

### 유효성 검사를 하여 readObject 메서드를 정의한 경우
````java
private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {

    // 불변식을 만족하는지 검사한다.
    if (start.compareTo(end) > 0) {
        throw new InvalidObjectException(start + "after" + end);
    }
}
````


### 방어적 복사를 하지않아 불변객체를 유지하지 못하는 경우
````java
public class MutablePeriod {
    // Period 인스턴스
    public final Period period;

    // 시작 시각 필드 - 외부에서 접근할 수 없어야 한다.
    public final Date start;

    // 종료 시각 필드 - 외부에서 접근할 수 없어야 한다.
    public final Date end;

    public MutablePeriod() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            // 유효한 Period 인스턴스를 직렬화한다.
            out.writeObject(new Period(new Date(), new Date()));

            /*
             * 악의적인 '이전 객체 참조', 즉 내부 Date 필드로의 참조를 추가한다.
             * 상세 내용은 자바 객체 직렬화 명세의 6.4절 참조.
             */
            byte[] ref = { 0x71, 0, 0x7e, 0, 5 }; // 참조 #5
            bos.write(ref); // 시작(start) 필드
            ref[4] = 4; // 참조 #4
            bos.write(ref); // 종료(end) 필드

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            period = (Period) in.readObject();
            start = (Date) in.readObject();
            end = (Date) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static void main(String[] args) {
        MutablePeriod mp = new MutablePeriod();
        Period p = mp.period;
        Date pEnd = mp.end;

        // 시간을 되돌린다.
        pEnd.setYear(78);
        System.out.println(p);

        // 60년대로 돌아간다.
        pEnd.setYear(69);
        System.out.println(p);
    }
}

# 실행 결과
Wed Nov 22 00:21:29 PST 2017 - Wed Nov 22 00:21:29 PST 1978
Wed Nov 22 00:21:29 PST 2017 - Sat Nov 22 00:21:29 PST 1969
````

### 방어적 복사와 유효성 검사를 모두 한 경우
````java
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();

    // 가변 요소들을 방어적으로 복사한다.
    start = new Date(start.getTime());
    end = new Date(end.getTime());

    // 불변식을 만족하는지 검사한다.
    if (start.compareto(end) > 0) {
        throw new InvalidObjectException(start + " after " + end);
    }
}
````

### readObject 메서드를 수정하지 않고 써도 좋을지 판단하는 방법
transient 필드를 제외한 모든 필드의 값을 매개벼수로 받아 유혀성 검사 없이 필드에 대입하는 public 생성자를 추가해도되면
<br> readObject 메서드를 수정하지않고 사용해도 된다.

### 유의사항
생성자 처럼 readObject 에서 또한 재정의 가능한 메서드를 호출하면 안된다.