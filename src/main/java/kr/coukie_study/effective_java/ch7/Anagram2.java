package kr.coukie_study.effective_java.ch7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Anagram2 {
  public static void main(String[] args) throws IOException {
      //dictionary : [abc, bac, cab, ab, ba]
      Path dictionary = Paths.get("./dictionary");
      int minGroupSize = 3;
      try (Stream<String> words = Files.lines(dictionary)) {
          words.collect(
                  Collectors.groupingBy(
                          word -> word
                                  .chars()
                                  .sorted()
                                  .collect(StringBuilder::new, (sb, c) -> sb.append((char) c), StringBuilder::append).toString())
          ).values()
                  .stream()
                  .filter(group -> group.size() >= minGroupSize)
                  .map(group -> group.size() + ": " + group)
                  .forEach(System.out::println);
      }
  }
}
