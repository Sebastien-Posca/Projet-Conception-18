package fr.unice.polytech.cookieFactory.recipe;

import java.util.ArrayList;
import java.util.List;

public class CookieRecipeBuilder {

    private IngredientCatalog ingredientCatalog;

    public CookieRecipeBuilder(IngredientCatalog ingredientCatalog){
        this.ingredientCatalog = ingredientCatalog;
    }

    /**
     * builds a Cookie recipe from string description
     * defaults dough to "Plain dough"
     * no flavour by default
     * defaults topping to "Vanilla"
     * defaults Mix to "MIXED"
     * defaults cooking to "CRUNCHY"
     * @param name of the recipe
     * @param description content of the recipe
     * @return a recipe
     */
    public CookieRecipe build(String name, String description) {

        List<Ingredient> ingredients = new ArrayList<>(5);

        if (description.isEmpty()) {
            pushIngredient(ingredients, "Plain dough");
            pushIngredient(ingredients, "Vanilla");

            return new CookieRecipe(name, ingredients, CookieRecipe.Mix.MIXED,
                    CookieRecipe.Cooking.CRUNCHY);
        }

        String[] splittedDescription = description.split(",");

        for (int j = 0; j < splittedDescription.length; j++) {
            splittedDescription[j] = splittedDescription[j].trim().toUpperCase();
        }

        int i = 0;
        //DOUGH
        pushIngredientOfType(ingredients, "dough", splittedDescription[i]);


        //---- FLAVOUR AND TOPPING ----
        Ingredient ingredient = ingredientCatalog.getIngredient(splittedDescription[++i]);
        checkIngredient(splittedDescription[i], ingredient);

        if(!(ingredient.getType().equals("FLAVOUR")
                || ingredient.getType().equals("TOPPING"))){
            throw new IllegalArgumentException("the second element must be a flavour or a topping.\n");
        }
        ingredients.add(ingredient);

        int max_remaining_flavour_count = CookieRecipe.maxToppingCount;
        if(ingredient.getType().equals("TOPPING")) max_remaining_flavour_count --;

        ingredient = ingredientCatalog.getIngredient(splittedDescription[++i]);

        if(ingredient != null){
            while(ingredient.getType().equals("TOPPING") && max_remaining_flavour_count != 0){
                ingredients.add(ingredient);
                ingredient = ingredientCatalog.getIngredient(splittedDescription[i]);
                if(ingredient == null)
                    break;

                max_remaining_flavour_count --;
                i ++;
            }
        }

        CookieRecipe.Mix mix;
        try{
            mix = CookieRecipe.Mix.valueOf(splittedDescription[i]);

        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(splittedDescription[i] + " is not a mix.\n");
        }

        i++;

        CookieRecipe.Cooking cooking;
        try{
            cooking = CookieRecipe.Cooking.valueOf(splittedDescription[i]);

        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(splittedDescription[i] + " is not a cooking.\n");
        }

        return new CookieRecipe(name, ingredients,mix, cooking);
    }

    private void pushIngredient(List<Ingredient> ingredients, String name) {
        Ingredient ingredient = ingredientCatalog.getIngredient(name);
        if(ingredient != null){
            ingredients.add(ingredient);
        }
    }

    private void pushIngredientOfType(List<Ingredient> ingredients, String typename, String name){
        List<Ingredient> ingredientsOfType = ingredientCatalog.getIngredientsByType(typename);

        if(ingredientsOfType == null){
            throw new IllegalArgumentException("the ingredient type " + typename + " does not exist !");
        }

        for(Ingredient currentIngredient : ingredientsOfType){
            if(currentIngredient.getName().equals(name)){
                ingredients.add(currentIngredient);
                return;
            }
        }

        throw new IllegalArgumentException("the ingredient " + name + " does not exist !\n");
    }

    private void checkIngredient(String name, Ingredient ingredient){
        if(ingredient == null){
            throw new IllegalArgumentException("the ingredient " + name + " does not exist !\n");
        }
    }


}
