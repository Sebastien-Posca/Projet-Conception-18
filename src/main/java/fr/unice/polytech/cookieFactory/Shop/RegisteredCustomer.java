package fr.unice.polytech.cookieFactory.Shop;

import fr.unice.polytech.cookieFactory.UnfaithPass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegisteredCustomer extends Customer {

    private boolean hasDiscount;
    private int cookieCount;
    private boolean isFidelityMember;
    private String password;

    /**
     * @param email
     * @param password
     */
    public RegisteredCustomer(String email, String password) {
        super(email);
        this.hasDiscount = false;
        this.cookieCount = 0;
        this.isFidelityMember = false;
        this.password = password;
    }

    /**
     * @param itemOrders
     */
    public void updateCookieCount(List<ItemOrder> itemOrders) {
        itemOrders.forEach(itemOrder -> this.cookieCount = this.cookieCount + itemOrder.getCount());
        if (cookieCount >= 30) {
            hasDiscount = true;
            cookieCount = 0;
        }
    }

    public boolean hasDiscount() {
        return hasDiscount;
    }

    public boolean useDiscount() {
        if (this.hasDiscount) {
            this.hasDiscount = false;
            this.cookieCount = 0;
            return true;
        } else {
            return false;
        }
    }

    public int getCookieCount() {
        return this.cookieCount;
    }

    public void subscribeToFidelityProgram() {
        this.isFidelityMember = true;
    }

    public boolean isFidelityMember() {
        return isFidelityMember;
    }

    public void setDiscountTrue() {
        hasDiscount = true;
    }

    public static class ShopCatalog {

        private List<Shop> shopList;

        public ShopCatalog() {
            shopList = new ArrayList<>();
        }

        public Shop addShop(Shop shop, UnfaithPass unfaithPass) {
            String shopLocation = shop.getLocation().trim().toLowerCase();

            for (Shop storedShop : shopList) {
                String currentShopLocation = storedShop.getLocation().trim().toLowerCase();

                if (shopLocation.equals(currentShopLocation)) {
                    return storedShop;
                }
            }

            shop.setUnfaithPass(unfaithPass);
            shopList.add(shop);

            return shop;
        }

        public Shop getShop(int shopId) {
            List<Shop> foundShops = shopList.stream()
                    .filter(shop -> shop.getShopId() == shopId)
                    .collect(Collectors.toList());
            if (foundShops.isEmpty())
                return null;
            return foundShops.get(0);
        }

        public Stream<Shop> stream(){
            return shopList.stream();
        }
    }
}