package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeBuilder;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;
import fr.unice.polytech.cookieFactory.Shop.ItemOrder;
import fr.unice.polytech.cookieFactory.Shop.RegisteredCustomer;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RegisteredCustomerTest {

    private RegisteredCustomer registeredCustomer;
    private List<ItemOrder> orderList;

    @Before
    public void setUp() throws Exception {
        this.registeredCustomer = new RegisteredCustomer("test@email.com", "password");
        //Ingredient.addIngredient("type", "name");
        orderList = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();

        IngredientCatalog ingredientCatalog = IngredientCatalog.newDefaultCatalog();

        ingredients.add(ingredientCatalog.getIngredient("name"));

        CookieRecipe cookieRecipe = new CookieRecipeBuilder(ingredientCatalog)
                                        .build("Choco","");
        ItemOrder itemOrder = new ItemOrder(cookieRecipe, 30);
        orderList.add(itemOrder);

    }

    @Test
    public void hasDiscount_case_before_purchase(){
        assertFalse(this.registeredCustomer.hasDiscount());
    }

    @Test
    public void hasDiscount_case_after_purchase() {
        this.registeredCustomer.updateCookieCount(orderList);
        assertTrue(this.registeredCustomer.hasDiscount());
    }

    @Test
    public void useDiscount() {
       /* assertFalse(this.registeredCustomer.useDiscount());
        this.registeredCustomer.updateCookieCount(orderList);
        System.out.println(this.registeredCustomer.getCookieCount());
        assertEquals(30, this.registeredCustomer.getCookieCount());
        assertTrue(this.registeredCustomer.useDiscount());
        assertEquals(0, this.registeredCustomer.getCookieCount());*/
    }

    @Test
    public void subscribeToFidelityProgram() {
        assertFalse(this.registeredCustomer.isFidelityMember());
        this.registeredCustomer.subscribeToFidelityProgram();
        assertTrue(this.registeredCustomer.isFidelityMember());
    }
}