package fr.unice.polytech.cookieFactory.catalogs;

import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeCatalog;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class CookieRecipeCatalogTest {

    CookieRecipeCatalog cookieRecipeCatalog = new CookieRecipeCatalog(IngredientCatalog.newDefaultCatalog());

    @Test
    public void addShop() {

        //checks that the recipe is not duplicated
        CookieRecipe cookieRecipe = cookieRecipeCatalog.addRecipe("Choco", "");
        assertSame(cookieRecipe, cookieRecipeCatalog.addRecipe("Choco", ""));
    }

}
