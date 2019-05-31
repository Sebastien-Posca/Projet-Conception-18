package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.recipe.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class Supplier {

    private Map<Ingredient, Integer> ingredientPriceMap = new HashMap<>();
    private String name;
    private int supplierId;

    private static int next_supplierId = 0;

    public Supplier(String name, Map<Ingredient, Integer> priceTable){
        this.name = name;
        this.ingredientPriceMap = priceTable;
        this.supplierId = next_supplierId++;
    }

    public void setIngredientPrice(Ingredient ingredient, int price) {
        this.ingredientPriceMap.put(ingredient,price);
    }

    public int getIngredientPrice(Ingredient ingredient){
        return this.ingredientPriceMap.get(ingredient);
    }

    public String getName(){
        return this.name;
    }

    public Map<Ingredient, Integer> getPriceTable(){
        return this.getPriceTable();
    }

    public int getSupplierId(){
        return this.supplierId;
    }

}
