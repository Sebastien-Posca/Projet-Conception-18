package fr.unice.polytech.cookieFactory;

import cucumber.api.java.sl.In;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeBuilder;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CookieRecipeBuilderTest {

    CookieRecipeBuilder recipeBuilder;

    @Before
    public void setUp(){
        IngredientCatalog catalog = IngredientCatalog.newDefaultCatalog();

        recipeBuilder = new CookieRecipeBuilder(catalog);
    }

    @Test
    public void build_case_empty() {
        CookieRecipe recipe = recipeBuilder.build("name","");

        assertEquals("name",recipe.getName());
    }

    @Test
    public void build_case_no_flavour_one_topping(){
        recipeBuilder.build("SimpleVanilla","plain dough, White chocolate topping, mixed, crunchy");
    }

    @Test
    public void build_case_no_flavour_several_toppings(){
        recipeBuilder.build(
                "SimpleVanilla",
                "plain dough, Milk chocolate topping, M&M’s™ topping, mixed, crunchy"
        );
        recipeBuilder.build(
            "SimpleVanilla",
            "plain dough, White chocolate topping, Milk chocolate topping, M&M’s™ topping, mixed, crunchy"
        );
    }


    @Test
    public void build_case_with_flavour_one_topping(){

        recipeBuilder.build(
            "SimpleVanilla",
            "plain dough, Vanilla, White chocolate topping, mixed, crunchy"
        );
    }


    @Test
    public void build_case_with_flavour_several_toppings(){


        recipeBuilder.build(
                "SimpleVanilla",
                "plain dough, Vanilla, Milk chocolate topping, M&M’s™ topping, mixed, crunchy"
        );
        recipeBuilder.build(
                "SimpleVanilla",
                "plain dough, Vanilla, White chocolate topping, Milk chocolate topping, M&M’s™ topping, mixed, crunchy"
        );
    }


}