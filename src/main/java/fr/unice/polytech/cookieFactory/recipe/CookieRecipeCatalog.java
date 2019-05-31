package fr.unice.polytech.cookieFactory.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CookieRecipeCatalog {

    private CookieRecipeBuilder recipeBuilder;
    private List<CookieRecipe> cookieRecipeList;

    
    public CookieRecipeCatalog(IngredientCatalog ingredientCatalog) {
        this.cookieRecipeList = new ArrayList<>();
        this.recipeBuilder = new CookieRecipeBuilder(ingredientCatalog);
    }

    public CookieRecipe addRecipe(String name, String description) {
        CookieRecipe cookieRecipe = recipeBuilder.build(name, description);
        for (CookieRecipe currentRecipe : cookieRecipeList) {
            if (currentRecipe.haveSameIngredients(cookieRecipe))
                return currentRecipe;
        }
        cookieRecipeList.add(cookieRecipe);
        return cookieRecipe;
    }

    public CookieRecipe getRecipe(String name) {
        List<CookieRecipe> foundRecipes = cookieRecipeList.stream()
                .filter(recipe -> recipe.getName().equals(name))
                .collect(Collectors.toList());
        if (foundRecipes.isEmpty())
            return null;
        return foundRecipes.get(0);
    }

}
