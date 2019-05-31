package fr.unice.polytech.cookieFactory.recipe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CookieRecipe {

    public static int maxToppingCount = 3;

    private String name;
    private List<Ingredient> ingredients;
    private Mix mix;
    private Cooking cooking;

    CookieRecipe(String name, List<Ingredient> ingredients, Mix mix, Cooking cooking) {
        this.name = name;
        ingredients.sort(Comparator.comparingInt(Ingredient::hashCode));
        this.ingredients = ingredients;
        this.mix = mix;
        this.cooking = cooking;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public Mix getMix() {
        return mix;
    }

    public Cooking getCooking() {
        return cooking;
    }

    enum Mix {
        MIXED, TOPPED;
    }

    enum Cooking {
        CRUNCHY, CHEWY;
    }

    public boolean haveSameIngredients(CookieRecipe cookieRecipe){
        return cookieRecipe.ingredients.equals(cookieRecipe.ingredients);
    }
}