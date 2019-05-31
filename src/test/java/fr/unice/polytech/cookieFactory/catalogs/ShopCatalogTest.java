package fr.unice.polytech.cookieFactory.catalogs;

import fr.unice.polytech.cookieFactory.Shop.RegisteredCustomer;
import fr.unice.polytech.cookieFactory.Shop.Shop;
import fr.unice.polytech.cookieFactory.UnfaithPass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class ShopCatalogTest {

    Shop shop;
    UnfaithPass unfaithPass;


    @Test
    public void addShop() {
        RegisteredCustomer.ShopCatalog shopCatalog = new RegisteredCustomer.ShopCatalog();
        unfaithPass = new UnfaithPass();

        shop = new Shop("08 Avenue Clémenceau, Paris", 0.20,1);
        Shop sameShop = new Shop("08 Avenue Clémenceau, Paris", 0.20,1);

        //checks that the shop is not duplicated
        shopCatalog.addShop(shop, unfaithPass);
        assertSame(shop, shopCatalog.addShop(sameShop, unfaithPass));
    }


    @Test
    public void getShop() {
        Shop shop = new Shop("", 0.20,1);


        RegisteredCustomer.ShopCatalog shopCatalog = new RegisteredCustomer.ShopCatalog();
        shopCatalog.addShop(shop, unfaithPass);
        assertSame(shop, shopCatalog.getShop(shop.getShopId()));
    }
}