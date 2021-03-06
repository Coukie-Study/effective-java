# 스레드보다는 실행자, 태스크, 스트림을 애용하라

- Thread 클래스를 직접 쓰기보다는 실행자(ExecutorService)를 쓰자.
    - 다양한 기능들이 있다.
        - 특정 태스크를 완료되기를 기다린다.(get)
        - 태스크 모음 중 아무 것 하나(invokeAny), 혹은 모든 태스크(invokeAll)가 완료되길 기다린다.
        - 실행자 서비스가 종료하기를 기다린다.(awaitTermination 메서드)
        - 완료된 태스크들의 결과를 차례로 받는다.(ExecutorCompletionService)
        - 태스크를 특정 시간에 혹은 주기적으로 실행하게 한다.
- 큐를 둘 이상의 스레드가 처리하게 하고 싶다면 간단히 다른 정적 팩터리를 이용하여 다른 종류의 실행자 서비스를 생성(스레드풀)하면 된다.
    - 스레드 갯수를 고정할 수도 필요에 따라 늘어나거나 줄일 수 있다.
    - 필요한 실행자 대부분은 Executors의 정적 팩터리를 이용해 만들 수 있다.
- 가벼운 프로덕션 서버라면 Executors.newCachedThreadPool이 일반적으로 좋다.
    - 다만 무거운 프로덕션 서버라면 새로운 태스크가 만들어지는 대로 스레드가 생성되어 서버에 부담을 준다.
- 무거운 프로덕션 서버라면 스레드 개수를 고정한 Executors.newFixedThreadPool를 선택하거나 직접 TheadPoolExecutor를 만들어 사용하는 편이 좋다.
- ExecutorService는 직접 스레드를 다루지 않고 태스크를 다룬다.
    - Runnable : 반환 X
    - Callable : 반환 O, 예외
- 자바9에서는 포크 조인 태스크 지원
    - 태스크를 작은 단위로 나눠 병렬로 실행하고 합칠 수 있는 태스크
    - 병렬 스트림을 이용하면 이점을 잘 얻을 수 있다.
