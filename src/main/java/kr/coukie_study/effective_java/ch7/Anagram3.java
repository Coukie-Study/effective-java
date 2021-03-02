package kr.coukie_study.effective_java.ch7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Anagram3 {
  public static void main(String[] args) throws IOException {
    //dictionary : [abc, bac, cab, ab, ba]
    Path dictionary = Paths.get("./dictionary");
    int minGroupSize = 3;
    try (Stream<String> words = Files.lines(dictionary)) {
      words.collect(Collectors.groupingBy(word -> alphabetize(word))).values().stream()
          .filter(group -> group.size() >= minGroupSize)
          .forEach(g -> System.out.println(g.size() + ": " + g));
    }
  }

  private static String alphabetize(String s) {
    char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
  }
}
