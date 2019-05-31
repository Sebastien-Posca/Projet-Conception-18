package fr.unice.polytech.cookieFactory.Shop;

import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;

public class ItemOrder {

    private CookieRecipe recipee;
    private int count;
    private boolean isPerso=false;


    /**
     * @param cookieRecipe
     * @param count
     */
    public ItemOrder(CookieRecipe cookieRecipe, int count) {
        this.recipee = cookieRecipe;
        this.count = count;
    }

    public String toString() {
        return recipee.getName() + " | quantity : " + count;
    }

    public int getCount() {
        return this.count;
    }

    public CookieRecipe getCookieRecipe() {
        return this.recipee;
    }

    public void setPerso(boolean perso) {
        isPerso = perso;
    }

    public boolean isPerso() {
        return isPerso;
    }
}