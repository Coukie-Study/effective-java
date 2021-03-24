# 예외의 상세 메세지에 실패 관련 정보를 담으라

#### 예외 추적(stackTrace)
- 예외를 잡지 못해 프로그램이 실패하면 자바 시스템은 그 예외의 스택 추적 정보를 자동으로 출력한다.
- 스택 추적은 예외 객체의 toString 메서드를 호출해 얻은 문자열이다.
    - 보통 클래스 이름 뒤에 상세 메세지가 붙은 형태이다.
- 이 정보는 프로그래머가 예외에 관해 얻을 수 있는 유일한 정보인 경우가 많다.
- 그러므로 예외 객체의 toString 메서드에 실패 원인에 관한 정보를 가능한 많이 담아 반환하는것은 매우 중요하다.

#### toString 작성 방법
- 실패 순간을 포착하려면 발생한 예외에 관련된 모든 매개변수와 필드의 값을 실패 메세지에 담아야 한다.
    - IndexOutOfBoundException의 경우 상세 메세지는 범위의 최솟값, 최댓값, 범위를 벗어났다는 인덱스의 값을 담아야 한다.
- 관련 데이터를 모두 담아야 하지만 장황할 필요는 없다.
    - 문제를 분석하는 사람은 스택 추적뿐 아니라 관련 문서와 소스코드를 함께 살펴본다. 그러므로 문서화 되어있는 정보까지 장황하게 메세지에 담을 필요는 없다.
- 예외 메세지를 보게되는 사용자는 주로 문제를 분석해야 할 프로그래머와 엔지니어 이므로 메세지의 가독성보다 담길 내용이 훨씬 중요하다.
- 예외 생성자에서 필요한 정보를 받아 상세 메세지까지 작성해놓는 방법도 괜찮다.
    - IndexOutOfBoundsException 생성자는 String을 받지만 다음과 같이 구현했어도 괜찮았을것이다.
    ````java
        /**
         * IndexOutOfBoundsException을 생성한다.
         *
         * @param lowerBound 인덱스의 최솟값
         * @param upperBound 인덱스의 최댓값 + 1
         * @param index 인덱스의 실젯값
         */
        public IndexOutOfBoundsException(int lowerBound, int upperBound,
            int index) {
          // 실패를 포착하는 상세 메시지를 생성한다.
          super(String.format(
              "최솟값: %d, 최댓값: %d, 인덱스: %d",
              lowerBound, upperBound, index));
      
          // 프로그램에서 이용할 수 있도록 실패 정보를 저장해둔다.
          this.lowerBound = lowerBound;
          this.upperBound = upperBound;
          this.index = index;
        }
    ````