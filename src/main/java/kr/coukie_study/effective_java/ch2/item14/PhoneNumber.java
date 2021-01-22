package kr.coukie_study.effective_java.ch2.item14;

import java.util.Comparator;
import static java.util.Comparator.comparingInt;

public class PhoneNumber implements Comparable<PhoneNumber> {
  private short areaCode;
  private short prefix;
  private short lineNum;

  private static final Comparator<PhoneNumber> COMPARATOR =
      comparingInt((PhoneNumber pn) -> pn.areaCode)
          .thenComparingInt((pn) -> pn.prefix)
          .thenComparingInt((pn) -> pn.lineNum);

  @Override
  public int compareTo(PhoneNumber pn) {
    return COMPARATOR.compare(this, pn);
  }
}
