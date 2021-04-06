# 자바 직렬화의 대안을 찾으라

- 객체 직렬화란 자바가 객체를 바이트 스트림으로 인코딩하고(직렬화) 그 바이트 스트림으로 부터 다시 객체를 재구성하는 (역직렬화) 메커니즘이다.
- 직렬화된 객체는 다른 VM에 전송하거나 디스크에 저장한 후 나중에 역직렬화할 수 있다.



### 보안문제

- 프로그래머가 어렵지 않게 분산 객체를 만들 수 있다는 것은 매력적이였지만 보이지 않는 생성자, API와 구현 사이의 모호해진 경계, 성능, 보안 등 그 대가가 컸다.
- 이 보안 문제는 실제로도 우려한 만큼 심각한 것으로 밝혀 졌다.
- 2016년 11월에는 샌프란시스코 시영 교통국이 랜섬웨어 공격을 받아 요금 징수 시스템이 이틀간 마비되는 사태를 겪기도 했다.
- 직렬화의 근본적인 문제는 공격 범위가 너무 넓고 지속적으로 더 넓어져 방어하기 어렵다는 점이다.
- ObjectInputStream의 readObject 메서드를 호출하면서 객체가 역직렬화되기 때문이다.
- readObject 메서드는 클래스패스 안의 거의 모든 타입의 객체를 만들어 낼 수 있는 사실상 마법 같은 생성자다.
- 바이트 스트림을 역직렬화하는 과정에서 이 메서드는 그 타입들 안의 모든 코드를 수행할 수 있다.
- 즉 그 타입들의 코드 전체가 공격 범위에 들어간다는 뜻이다.

### 가젯(gadget)

- 공격자와 보안 전문가들은 자바 라이브러리와 널리 쓰이는 서드파티 라이브러리에서 직렬화 가능 타입들을 연구하여 역직렬화 과정에서 호출되어 잠재적으로 위험한 동작을 수행하는 메서드들을 찾아보았다. 이런 메서드들을 가젯이라 부른다.
- 여러 가젯을 함께 사용하여 가젯 체인을 구성할 수도 있는데, 가끔씩 공격자가 기반 하드웨어의 네이티브 코드를 마음대로 실행할 수 있는 아주 강력한 가젯 체인도 발견되곤 한다.
- 따라서 아주 신중하게 제작한 바이트 스트림만 역직렬화해야 한다.



### 직렬화 위험을 회피하는 가장 좋은 방법은 아무것도 역직렬화하지 않는 것이다.

- 우리가 작성하는 새로운 시스템에서 자바 직렬화를 써야 할 이유는 전혀 없다.
- 객체와 바이트 시퀀스를 변환해주는 다른 메커니즘이 많이 있다.
- 이 방식들은 자바 직렬화의 여러 위험을 회피하면서 다양한 플랫폼 지원, 우수한 성능, 풍부한 지원도구, 활발한 커뮤니티와 전문가 집단 등 수많은 이점까지 제공한다.

### 크로스-플랫폼 구조화된 데이터 표현(cross-platform structured-data representation)

- 이 표현들의 공통점은 자바 직렬화보다 훨씬 간단하다는 것이다.
- 임의 객체를 자동으로 직렬화/역직렬화하지 않는다.
- 속성-값 쌍의 집합으로 구성된 간단하고 구조화된 데이터 객체를 사용한다.
- 크로스-플랫폼 구조화된 데이터 표현의 선두주자는 JSON과 프로토콜 버퍼다.
- 둘의 큰 차이는 JSON은 텍스트 기반이라 사람이 읽을 수 있고, 프로토콜 버퍼는 이진 표현이라 효율이 훨씬 높다는 점이다.



### 신뢰할 수 없는 데이터는 절대 역직렬화하지 않는 것이다.

- 레거시 시스템 때문에 자바 직렬화를 완전히 배제할 수 없을 때의 차선책은 신뢰할 수 없는 데이터는 절대 역직렬화하지 않는 것이다.
- 직렬화를 피할 수 없고 역직렬화한 데이터가 안전한지 완전히 확신할 수 없다면 객체 역직렬화 필터링을 사용하자
- 객체 역직렬화 필터링은 데이터 스트림이 역직렬화 되기 전에 필터를 설치하는 기능이다.
- 클래스 단위로, 특정 클래스를 받아들이거나 거부할 수 있다.
- '기본수용' 모드에서는 블랙리스트에 기록된 잠재적으로 위험한 클래스들을 거부하고,'기본 거부' 모드에서는 화이트리스트에 기록된 안전하다고 알려진 클래스들만 수용한다.
- 블랙리스트 방식보다는 화이트 리스트 방식을 추천한다. 블랙리스트 방식은 이미 알려진 위험으로부터만 보호할 수 있기 때문이다.



### 정리

- 직렬화는 위험하니 피해야 한다.
- 시스템을 밑바닥부터 설계한다면 JSON이나 프로토콜버퍼 같은 대안을 사용하자
- 신뢰할 수 없는 데이터는 역직렬화하지 말자
- 꼭 해야한다면 객체 역직렬화 필터링을 사용하자.
