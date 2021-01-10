package kr.coukie_study.effective_java.ch1;

public class ParameterFactoryTest {
  public static void main(String[] args) throws Exception{
    Pet cat = Pet.getCatOrDog("cat");
    Pet dog = Pet.getCatOrDog("dog");
    cat.sound();
    dog.sound();
  }
}

class Pet {
  public void sound() {
  }

  public static Pet getCatOrDog (String pet) throws Exception{
    if (pet.equals("cat")) {
      return new Cat();
    } else if (pet.equals("dog")) {
      return new Dog();
    }
    throw new Exception("잘못된 입력");
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