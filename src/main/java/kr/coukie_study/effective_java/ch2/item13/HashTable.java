package kr.coukie_study.effective_java.ch2.item13;

public class HashTable {
  private Entry[] buckets = new Entry[10];

  private static class Entry {
    final Object key;
    Object value;
    Entry next;

    Entry(Object key, Object value, Entry next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }

    // 이 엔트리가 가리키는 연결 리스트를 재귀적으로 복사
    Entry deepCopy() {
      return new Entry(key, value, next == null ? null : next.deepCopy());
    }

    Entry deepCopy2(){
      Entry result = new Entry(key, value, next);
      for (Entry p = result; p.next != null; p = p.next){
        p.next = new Entry(p.next.key, p.next.value, p.next.next);
      }
      return result;
    }
  }
  // 재귀함수를 많이 이용하기때문에 스택 오버플로우를 발생 시킬 수 있다.
  @Override
  public HashTable clone() {
    try {
      HashTable result = (HashTable) super.clone();
      result.buckets = new Entry[buckets.length];
      for (int i = 0; i < buckets.length; i++) {
        if (buckets[i] != null) {
          result.buckets[i] = buckets[i].deepCopy();
        }
      }
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
