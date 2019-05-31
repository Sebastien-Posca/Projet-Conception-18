package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.recipe.Ingredient;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IngredientCatalogTest {

    IngredientCatalog catalog;

    @Before
    public void setUp(){
        catalog = new IngredientCatalog();


        catalog.addIngredients("DOUGH",
                "Plain dough", "Chocolate dough", "Peanut-Butter dough", "Oatmeal dough");
        catalog.addIngredients("FLAVOUR",
                "Vanilla", "Cinnamon", "Chili");
        catalog.addIngredients("TOPPING",
                "White chocolate topping", "Milk chocolate topping", "M&M’s™ topping", "Reese’s buttercup");
    }


    @Test
    public void addIngredient_case_existing_ingredient() {
        assertNull(catalog.addIngredient("DOUGH", "plain dough"));
    }

    @Test
    public void addIngredient_case_non_existing_ingredient() {
        assertNotNull(catalog.addIngredient("DOUGH", "Space dough"));
    }

    @Test
    public void addIngredients() {
        List<Ingredient> ingredientList = catalog.addIngredients("DOUGH", "new dough 1", "new dough 2");
        assertEquals(2, ingredientList.size());
        assertEquals("NEW DOUGH 1", ingredientList.get(0).getName());
        assertEquals("NEW DOUGH 2", ingredientList.get(1).getName());
    }

    @Test
    public void getIngredientsByType() {
        List<Ingredient> ingredients = catalog.getIngredientsByType("Dough");
        assertNotNull(ingredients);
        assertFalse(ingredients.isEmpty());
        assertTrue(ingredients.stream().allMatch(
                ingredient -> ingredient.getType().equals("DOUGH"))
        );
    }

    @Test
    public void getIngredient() {
        catalog.addIngredient("DOUGH", "NEW DOUGH 2");
        Ingredient ingredient = catalog.getIngredient("NEW DOUGH 2");
        assertEquals("DOUGH", ingredient.getType());
        assertEquals("NEW DOUGH 2", ingredient.getName());

    }

}