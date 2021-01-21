package kr.coukie_study.effective_java.ch1.item1;

public class ParameterFactoryTest {
  public static void main(String[] args) throws Exception {
    Pet cat = Pet.getCatOrDog("cat");
    Pet dog = Pet.getCatOrDog("dog");
    cat.sound();
    dog.sound();

  }
}

class Pet {
  public void sound() {}

  public static Pet getCatOrDog(String pet) {
    if ("cat".equals(pet)) {
      return new Cat();
    } else if ("dog".equals(pet)) {
      return new Dog();
    }
    throw new IllegalArgumentException("잘못된 입력");
  }
}

class Cat extends Pet {
  @Override
  public void sound() {
    System.out.println("야옹");
  }
}

class Dog extends Pet {
  @Override
  public void sound() {
    System.out.println("멍멍");
  }
}

