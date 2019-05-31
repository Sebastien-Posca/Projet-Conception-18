package fr.unice.polytech.cookieFactory.Cucumbers;

import cucumber.api.java8.En;
import fr.unice.polytech.cookieFactory.Shop.Shop;

public class GestionCookieFactory implements En {


    public GestionCookieFactory() {
        When("^le manager ajoute un magasin localisé à \"([^\"]*)\"$", (String arg0) -> {
            Shop shop = new Shop(arg0, 0.2, 1);
            CommonStepDefs.si.addShop(shop);
        });
        Then("^le magasin se trouve dans le catalogue de magasin$", () -> {
            assert CommonStepDefs.si.getShopById(0) != null;
        });
        When("^le manager ajoute un nouvel ingredient de type \"([^\"]*)\" appelé \"([^\"]*)\"$", (String arg0, String arg1) -> {
            CommonStepDefs.si.addIngredient(arg0, arg1);
        });
        Then("^le nouvel ingrédient \"([^\"]*)\" se trouve bien dans la liste des ingredients$", (String arg0) -> {
            assert CommonStepDefs.si.getIngredient(arg0) != null;
        });
        When("^le manager ajoute une nouvelle recette \"([^\"]*)\"$", (String arg0) -> {
            CommonStepDefs.si.addRecipe(arg0,"");
        });
        Then("^la nouvelle recette \"([^\"]*)\" se trouve dans le catalogue de recette$", (String arg0) -> {
            // Write code here that turns the phrase above into concrete actions
            assert CommonStepDefs.si.getRecipeByName(arg0) != null;
        });

    }
}
