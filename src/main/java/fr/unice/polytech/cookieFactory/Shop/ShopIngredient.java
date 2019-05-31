package fr.unice.polytech.cookieFactory.Shop;

import fr.unice.polytech.cookieFactory.Supplier;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;

public class ShopIngredient {

    private Shop shop;
    private Ingredient ingredient;
    private double margin;
    private Supplier supplier;
    private int stock;

    ShopIngredient(Shop shop, Ingredient ingredient, double margin, Supplier supplier, int stock) {
        this.shop = shop;
        this.ingredient = ingredient;
        this.margin = margin;
        this.supplier = supplier;
        this.stock = stock;
    }

    public Shop getShop() {
        return shop;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getMargin() {
        return margin;
    }

    public double getSupplierPrice() {
        return this.supplier.getIngredientPrice(this.ingredient);
    }

    public double computePrice() {
        return this.getSupplierPrice() * (1 + margin);
    }


    public void setMargin(double margin) {
        this.margin = margin;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void addStock(int stock){
        this.stock += stock;
    }

    public void removeStock(int stock){
        this.stock -= stock;
        if(this.stock < 0){this.stock=0;}
    }

    public int getStock(){
        return this.stock;
    }

    public Supplier getSupplier(){
        return this.supplier;

    }

}
