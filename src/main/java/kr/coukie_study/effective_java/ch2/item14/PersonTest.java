package kr.coukie_study.effective_java.ch2.item14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.util.Comparator.comparing;

public class PersonTest {
  public static void main(String[] args) {
    ArrayList<Person> list = new ArrayList<>();
    list.add(new Person(new Wallet(10)));
    list.add(new Person(new Wallet(5)));
    list.add(new Person(new Wallet(15)));
    Collections.sort(list);
    for (Person p : list) {
      System.out.println(p.wallet.money);
    }
  }
}

class Wallet implements Comparable<Wallet> {
  int money;

  public Wallet(int money) {
    this.money = money;
  }

  @Override
  public int compareTo(Wallet w) {
    return Integer.compare(money, w.money);
  }
}

class Person implements Comparable<Person> {
  Wallet wallet;

  public Person(Wallet wallet) {
    this.wallet = wallet;
  }

  private final Comparator<Person> COMPARATOR_ASCENDING = comparing((Person p) -> p.wallet);

  private final Comparator<Person> COMPARATOR_DESCENDING =
      comparing((Person p) -> p.wallet, (w1, w2) -> Integer.compare(w2.money, w1.money));

  @Override
  public int compareTo(Person p) {
    return COMPARATOR_ASCENDING.compare(this, p);
  }
}
