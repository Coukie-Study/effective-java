package kr.coukie_study.effective_java.ch1;

public class Box {
  private static final Box EMPTY_BOX = new EmptyBox();

  public static Box emptyBox() {
    return EMPTY_BOX;
  }

  public static Box box() {
    return new Box();
  }

  private static class EmptyBox extends Box {}
}
