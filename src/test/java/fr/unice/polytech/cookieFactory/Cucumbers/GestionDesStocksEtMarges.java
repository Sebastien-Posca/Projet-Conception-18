package fr.unice.polytech.cookieFactory.Cucumbers;

import cucumber.api.java8.En;
import fr.unice.polytech.cookieFactory.Shop.Shop;
import fr.unice.polytech.cookieFactory.Supplier;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class GestionDesStocksEtMarges implements En {


    public GestionDesStocksEtMarges() {
        Given("^un magasin$", () -> {
            Shop shop = new Shop("nice", 0.2, 1);
            CommonStepDefs.si.addShop(shop);
        });
        And("^une recette \"([^\"]*)\"$", (String arg0) -> {
            CommonStepDefs.si.addRecipe(arg0, "");
        });
        And("^un fournisseur \"([^\"]*)\" qui fournit l'ingredient \"([^\"]*)\" au magasin$", (String arg0, String arg1) -> {
            Map<Ingredient, Integer> ingredientIntegerMap = new HashMap<>();
            ingredientIntegerMap.put(CommonStepDefs.si.getIngredient(arg1), 1);
            Supplier supplier = new Supplier(arg0,ingredientIntegerMap);

            CommonStepDefs.si.addIngredientToShop(0, arg1, 1, supplier);
        });
        When("^un manager modifie la marge sur les cookies personalisés à la valeur (\\d+) dans le magasin$", (Integer arg0) -> {
            CommonStepDefs.si.setCustomRecipeMargin(0, arg0);
        });
        Then("^la marge sur les cookies personalisés dans ce magasin est égale à (\\d+)$", (Integer arg0) -> {
          assert  CommonStepDefs.si.getShopById(0).getCustomRecipeMargin() == arg0;
        });
        When("^un manager ajoute la recette du mois \"([^\"]*)\" dans le magasin$", (String arg0) -> {
            CommonStepDefs.si.setRecipeOfTheMonth(0,arg0, "");
        });

        Then("^le magasin possède bien \"([^\"]*)\" en temps que cookie du mois$", (String arg0) -> {
            assert CommonStepDefs.si.getRecipeByShop(0, arg0) != null;
        });
        When("^un manager change le fournisseur de l'ingredient \"([^\"]*)\" pour le fournisseur de nom \"([^\"]*)\"$", (String arg0, String arg1) -> {
            Map<Ingredient, Integer> ingredientIntegerMap = new HashMap<>();
            ingredientIntegerMap.put(CommonStepDefs.si.getIngredient(arg1), 2);
            Supplier supplier = new Supplier(arg1,ingredientIntegerMap);
            CommonStepDefs.si.getShopById(0).setIngredientSupplier(CommonStepDefs.si.getIngredient(arg0), supplier);
        });
        Then("^le fournisseur de \"([^\"]*)\" est bien \"([^\"]*)\"$", (String arg0, String arg1) -> {
            assert CommonStepDefs.si.getShopById(0).getShopIngredientByIngredient(CommonStepDefs.si.getIngredient(arg0)).getSupplier().getName().equals(arg1);
        });
        When("^un manager change la valeur du ratio de conversion euros-points par \"([^\"]*)\" de la carte d'infidélité$", (String arg0) -> {
            CommonStepDefs.si.setUnfaithPointEarnedRatio(0,Double.parseDouble(arg0));

        });
        Then("^le ratio est effectivement remplacé par le nouveau ratio de \"([^\"]*)\"$", (String arg0) -> {
            assert CommonStepDefs.si.getShopById(0).getUnfaithPointEarnedRatio() == Double.parseDouble(arg0);
        });
        When("^le manager modifie les horaires du magasin pour que l'ouverture soit \"([^\"]*)\" le \"([^\"]*)\"$", (String arg0, String arg1) -> {
            CommonStepDefs.si.addNewScheduleDayShop(LocalTime.parse(arg0), LocalTime.of(20,0,0), LocalDate.parse(arg1), 0);
        });
        Then("^les horaires du magasin sont bien modifié pour le \"([^\"]*)\", il ouvre bien à \"([^\"]*)\"$", (String arg0, String arg1) -> {
            String string = CommonStepDefs.si.getScheduleOfDayShop(0).getScheduleIntervalOfDay(LocalDate.parse(arg0)).toString();
            assert string.split("h")[0].equals(arg1.split(":")[0]);
        });


    }
}
