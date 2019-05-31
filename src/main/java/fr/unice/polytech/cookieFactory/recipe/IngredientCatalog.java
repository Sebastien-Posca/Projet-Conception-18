package fr.unice.polytech.cookieFactory.recipe;

import java.util.*;

public class IngredientCatalog {

    private final Map<String, List<Ingredient>> typenameToInstancesMap;

    public IngredientCatalog(){
        typenameToInstancesMap = new HashMap<>();
    }

    public static IngredientCatalog newDefaultCatalog(){
        return new IngredientCatalog(){{

            addIngredients("DOUGH",
                    "Plain dough", "Chocolate dough", "Peanut-Butter dough", "Oatmeal dough");
            addIngredients("FLAVOUR",
                    "Vanilla", "Cinnamon", "Chili");
            addIngredients("TOPPING",
                    "White chocolate topping", "Milk chocolate topping", "M&M’s™ topping", "Reese’s buttercup");
        }};
    }

    public Ingredient addIngredient(String typename, String name) {
        typename = typename.trim().toUpperCase();
        name = name.trim().toUpperCase();

        Ingredient ingredient = new Ingredient(typename, name);

        List<Ingredient> ingredientsOfType = typenameToInstancesMap.getOrDefault(typename, null);

        if (ingredientsOfType == null) {
            typenameToInstancesMap.put(typename, new ArrayList<Ingredient>() {{
                add(ingredient);
            }});
            return ingredient;
        }

        if (ingredientsOfType.stream().anyMatch(
                currentIngredient -> currentIngredient.getName().equals(ingredient.getName()))
        ) {
            return null;
        }

        ingredientsOfType.add(ingredient);
        return ingredient;
    }

    public List<Ingredient> addIngredients(String typename, String... names) {
        List<Ingredient> createdIngredients = new ArrayList<>();

        for (String name : names) {
            createdIngredients.add(addIngredient(typename, name));
        }

        return createdIngredients;
    }


    public List<Ingredient> getIngredientsByType(String typename) {
        typename = typename.trim().toUpperCase();
        List<Ingredient> ingredients = typenameToInstancesMap.getOrDefault(typename, null);
        if (ingredients == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(ingredients);
    }

    public Ingredient getFirstIngredientByType(String typename) {
        return getIngredientsByType(typename).get(0);
    }

    public Ingredient getIngredient(String name) {
        for (List<Ingredient> ingredients : typenameToInstancesMap.values()) {
            for (Ingredient ingredient : ingredients) {
                if (ingredient.getName().equals(name.toUpperCase()))
                    return ingredient;
            }
        }
        return null;
    }
}
