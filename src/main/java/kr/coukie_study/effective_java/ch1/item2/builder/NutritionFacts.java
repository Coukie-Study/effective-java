package kr.coukie_study.effective_java.ch1.item2.builder;

public class NutritionFacts {
  private final int servingSize;
  private final int servings;
  private final int calories;
  private final int fat;
  private final int sodium;
  private final int carbohydrate;

  public static class Builder {
    private final int servingSize;
    private final int servings;

    private int calories;
    private int fat;
    private int sodium;
    private int carbohydrate;

    public Builder(int servings, int servingSize) {
      this.servings = servings;
      this.servingSize = servingSize;
    }

    public Builder calories(int value) {
      calories = value;
      return this;
    }

    public Builder fat(int value) {
      fat = value;
      return this;
    }

    public Builder sodium(int value) {
      sodium = value;
      return this;
    }

    public Builder carbohydrate(int value) {
      carbohydrate = value;
      return this;
    }

    public NutritionFacts build() {
      return new NutritionFacts(this);
    }
  }

  private NutritionFacts(Builder builder) {
    servingSize = builder.servingSize;
    servings = builder.servings;
    calories = builder.calories;
    fat = builder.fat;
    sodium = builder.sodium;
    carbohydrate = builder.carbohydrate;
  }
}
