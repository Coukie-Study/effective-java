package kr.coukie_study.effective_java.ch7;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Anagram1 {
  public static void main(String[] args) throws IOException {
    //dictionary : [abc, bac, cab, ab, ba]
    File dictionary = new File("./dictionary");
    int minGroupSize = 3;

    Map<String, Set<String>> groups = new HashMap<>();
    try (Scanner s = new Scanner(dictionary)) {
      while(s.hasNext()) {
        String word = s.next();
        groups.computeIfAbsent(alphabetize(word), (unused) -> new TreeSet<>()).add(word);
      }
    }

    for (Set<String> group : groups.values()) {
      if (group.size() >= minGroupSize) {
        System.out.println(group.size() + ": " + group);
      }
    }
  }
  private static String alphabetize(String s){
    char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
  }
}
