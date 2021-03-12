# 전통적인 for 문보다는 for-each 문을 사용하라.
````java
for (Iterator<Element> i = c.iterator(); i.hasNext(); ){
    Elelemt e = i.next();
    ...
}

for (int i = 0; i < a.length; i++){
    ...
}
````
#### 전통적인 for문의 단점
- 코드가 지저분하다
    - 만약 필요한 값이 원소 뿐이라면 위 코드의 iterator와 인덱스 변수는 쓸데없는 코드이다.
- 오류가 생길 가능성이 높아진다
    - iterator와 인덱스 변수가 자주 등장하면 그만큼 오류가 생길 가능성이 높아진다.
- 컬렉션이냐 배열이냐에 따라 코드의 형태가 달라진다.

#### 향상된 for 문(for-each 문)
````java
for (Element e : elements){
    ...
}
````
위와 같이 사용하는 원소를 제외하고 순회하기 위한 iterator나 인덱스 변수가 사용되지 않는다.

#### 컬렉션을 중첩해서 순회하는 경우 for-each 문의 이점은 커진다.
````java
enum Suit {CLUB, DIAMOND, HEART, SPADE}
enum Rank {ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT,
           NINE, TEN, JACK, QUEEN, KING }

static Collection<Suit> suits = Arrays.asList(Suit.values());
static Collection<Rank> ranks = Arrays.asList(Rank.values());

List<Card> deck = new ArrayList<>();
for (Iterator<Suit> i = suits.iterator(); i.hasNext(); )
    for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
        deck.add(new Card(i.next(), j.next()));

````
위와 같은 경우 Suit가 Rank 하나당 불리므로 원하는 Deck을 만들 수 없을 뿐더러
<br> Rank를 다 순회하기 전에 Suit가 다 소진되므로 NoSuchElementException을 던진다.

````java
for (Iterator<Suit> i = suits.iterator(); i.hasNext(); )
    Suit suit = i.next();
    for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
        deck.add(new Card(suit, j.next()));
````
이처럼 문제를 해결 할 수 있지만 바깥에 원소를 저장하는 변수를 만들어야 한다.

#### for-each 를 활용한 해결법
````java
// for-each 문
for (Suit suit : suits)
    for (Rank rank : ranks)
        deck.add(new Card(suit, rank));
````

### for-each 문을 사용할 수 없는 상황
- 파괴적인 필터링(destructive filtering)
    - 컬렉션을 순회하면서 선택된 원소를 제거해야 한다면 Iterator의 remove를 호출해야 한다.<br>
    자바 8부터는 Collection의 removeIf 메서드를 사용하면 굳이 for문을 통해 순회하는 일 없이 해결할 수 있다.
- 변형(transforming) 
    - 리스트나 배열을 순회하면서 그 원소의 값 일부 혹은 전체를 교체해야 한다면 리스트의 반복자나 배열의 인덱스를 사용해야 한다.
- 병렬 반복(parallel iteration)
    - 여러 컬렉션을 병렬로 순회해야 한다면 각각 반복자와 인덱스 변수를 사용해 엄격하고 명시적으로 제어해야 한다.