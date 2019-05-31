package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.Shop.*;
import fr.unice.polytech.cookieFactory.order.Order;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeCatalog;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class SI {


    private IngredientCatalog ingredientCatalog;
    private CookieRecipeCatalog cookieRecipeCatalog;
    private RegisteredCustomer.ShopCatalog shopCatalog;
    private CustomerCatalog customerCatalog;
    private UnfaithPass unfaithPass;


    public SI() {
        customerCatalog = new CustomerCatalog();
        shopCatalog = new RegisteredCustomer.ShopCatalog();
        ingredientCatalog = new IngredientCatalog();
        unfaithPass = new UnfaithPass();


        ingredientCatalog.addIngredients("DOUGH",
                "Plain dough", "Chocolate dough", "Peanut-Butter dough", "Oatmeal dough");
        ingredientCatalog.addIngredients("FLAVOUR",
                "Vanilla", "Cinnamon", "Chili");
        ingredientCatalog.addIngredients("TOPPING",
                "White chocolate topping", "Milk chocolate topping", "M&M’s™ topping", "Reese’s buttercup");
        cookieRecipeCatalog = new CookieRecipeCatalog(ingredientCatalog);
    }


    //Customer related methods *****************************************************************************************
    public int logIn(String emailAddress, String password) {
        Customer customer = customerCatalog.getCustomer(emailAddress);
        if (customer == null) return -1;
        return customer.getCustomerId();
    }

    public Customer getCustomerById(int customerId) {
        return customerCatalog.getCustomer(customerId);
    }

    public Customer getCustomerByEmail(String customerEmail) {
        return customerCatalog.getCustomer(customerEmail);
    }

    public String signIn(String email, String password) {
        if (customerCatalog.getCustomer(email) != null) {
            return "There is already an account associated with the " + email + " address.";
        }
        Customer newCustomer = new RegisteredCustomer(email, password);
        customerCatalog.addCustomer(newCustomer);
        return newCustomer.getCustomerId() + "";
    }

    public String bindUnfaithPassId(int unfaithPassId, int customerId) {
        return customerCatalog.bindUnfaithPassId(unfaithPassId, customerId);
    }

    //Shop related methods *********************************************************************************************
    public void addShop(Shop shop) {
        shopCatalog.addShop(shop, unfaithPass);
    }

    public Shop getShopById(int id) {
        return shopCatalog.getShop(id);
    }

    public void changeScheduleOfDayShop(LocalTime openingTime, LocalTime closingTime, LocalDate day, int shopId) {
        shopCatalog.getShop(shopId).changeScheduleOfDay(openingTime, closingTime, day);
    }

    public Schedule getScheduleOfDayShop(int shopId) {
        return shopCatalog.getShop(shopId).getShopSchedule();
    }

    public void addNewScheduleDayShop(LocalTime openingTime, LocalTime closingTime, LocalDate newDate, int shopId) {
            try {
                shopCatalog.getShop(shopId).addNewScheduleDay(openingTime, closingTime, newDate);
            } catch(Exception e) {
                System.err.println(e.toString());
            }
    }

    public void setTaxeRate(int shopId, int taxeRate) {
        shopCatalog.getShop(shopId)
                .setTaxeRate(taxeRate);
    }

    public String addRecipe(String name, String description) {
        cookieRecipeCatalog.addRecipe(name, description);
        return "recipe successfully added";
    }

    public String addRecipeToShop(int shopId, String name) {
        CookieRecipe cookieRecipe = cookieRecipeCatalog.getRecipe(name);
        if (cookieRecipe == null) return "CookieRecipe named '" + name + "' does not exist.";

        shopCatalog.getShop(shopId).addRecipe(cookieRecipe);
        return "recipe successfully added";
    }

    public CookieRecipe getRecipeByShop(int shopId, String name) {
        CookieRecipe cookieRecipe = this.shopCatalog.getShop(shopId).getRecipe(name);
        if (cookieRecipe == null) {
            throw new IllegalArgumentException();
        }
        return cookieRecipe;
    }

    public CookieRecipe getRecipeByName(String name){
        return this.cookieRecipeCatalog.getRecipe(name);
    }

    public void setIngredientMargin(int shopId, Ingredient ingredient, double margin){
        this.shopCatalog.getShop(shopId).setIngredientMargin(ingredient, margin);
    }

    public void setCustomRecipeMargin(int shopId, double margin){
        this.shopCatalog.getShop(shopId).setCustomRecipeMargin(margin);
    }

    public void setSupplierForIngredientByShop(int shopId, Ingredient ingredient, Supplier supplier) {
        this.shopCatalog.getShop(shopId).setIngredientSupplier(ingredient, supplier);
    }

    public void addStockToShop(Ingredient ingredient, int stock, int shopId) {
        this.shopCatalog.getShop(shopId).addStock(ingredient, stock);
    }

    public void removeStockToShop(Ingredient ingredient, int stock, int shopId) {
        this.shopCatalog.getShop(shopId).removeStock(ingredient, stock);
    }

    public double getPrice(int shopId, List<ItemOrder> cookieCount) {
        return shopCatalog.getShop(shopId).getPrice(cookieCount);
    }

    public void setRecipeOfTheMonth(int shopId, String name, String descritpion) {
        this.shopCatalog.getShop(shopId).setRecipeOfTheMonth(cookieRecipeCatalog.addRecipe(name, descritpion));
    }

    public ItemOrder createItemOrder(int shopId, String recipeName, int count) {
        CookieRecipe cr = this.getRecipeByName(recipeName);
        return this.shopCatalog.getShop(shopId).createItemOrder(cr, count);
    }

    /**
     * Allows the Cookie Factory company to add a new kind of ingredient
     * @param typename
     * @param name
     * @return
     */
    public String addIngredient(String typename, String name) {
        Ingredient ingredient = ingredientCatalog.addIngredient(typename, name);
        if(ingredient != null)
            return "ingredient of type " + typename + " and name " + name + " has been successfully added";
        return  "ingredient has not been added";
    }

    public String addIngredientToShop(int shopId, String name, double margin, Supplier supplier) {
        this.shopCatalog.getShop(shopId).addIngredient(ingredientCatalog.getIngredient(name), margin, supplier);
        return "ingredient of type "+ name + " has been successfully added";
    }

    /**
     * change the ratio of earning unFaith pass points.
     * @param shopId
     * @param ratio
     * @return
     */
    public String setUnfaithPointEarnedRatio(int shopId, double ratio){
        this.getShopById(shopId).setUnfaithPointEarnedRatio(ratio);
        return "ratio successfully chnaged";
    }

    public Ingredient getIngredient(String name){
        return ingredientCatalog.getIngredient(name);
    }


    //Order related methods ********************************************************************************************

    public List<Order> getPendingOrders(int shopId) {
        return shopCatalog.getShop(shopId).getPendingOrders();
    }

    public String deliverOrder(int shopId, int orderId) {
        Shop shop = shopCatalog.getShop(shopId);

        if (shop == null) return "the shop id " + shopId + " does not exist";
        shop.deliverOrder(orderId);
        return "delivery success";

    }

    /**
     * set the order ready to be delivered
     * @param orderId
     * @param shopId
     * @return a confirmation
     */
    public String orderIsNowReady(int orderId, int shopId) {
        if (this.shopCatalog.getShop(shopId).orderIsNowReady(orderId)) {
            return "The order n°" + orderId + "is now ready";
        } else {
            return "Cannot be ready";
        }
    }


    public String cancelOrder(int orderId, int shopId) {
        if (this.shopCatalog.getShop(shopId).cancelOrder(orderId)) {
            return "The order n°" + orderId + "is canceled";
        }
        return "failed";
    }

    /**
     * start to make the order
     * @param orderId
     * @param shopId
     * @return confirmation
     */
    public String startProcessingOrder(int orderId, int shopId) {
        if (this.shopCatalog.getShop(shopId).startProcessingOrder(orderId)) {
            return "The order n°" + orderId + "is now processing";
        } else {
            return "failed";
        }
    }

    public String removeOrder(int shopId, int orderId) {
        shopCatalog.getShop(shopId).getOrders().remove(orderId);
        return "Order successfully removed";
    }

    public String payOrderOnline(int shopId, int orderId, int creditCardNumber) {
        shopCatalog.getShop(shopId).payOrderOnline(orderId, creditCardNumber);
        return "order payed online";
    }

    public String payOrderAtCounterUnfaithPass(int shopId, int orderId, boolean useMoney, boolean getBonus, int unfaithPassId) {
        shopCatalog.getShop(shopId).payOrderAtCounterWithPass(orderId,useMoney, getBonus, unfaithPassId);
        return "order payed at counter";
    }

    public String payOrderAtCounterCash(int shopId, int orderId) {
        shopCatalog.getShop(shopId).payOrderAtCounterWithCash(orderId);
        return "order payed at counter";
    }

    public String payOrderAtCounterCredidCard(int shopId, int orderId, int creditCardNumber) {
        shopCatalog.getShop(shopId).payOrderAtCounterWithCreditCard(orderId, creditCardNumber);
        return "order payed at counter";
    }

    /**
     * place the order at the counter case when you're not an user
     * @param itemOrders
     * @param shopId
     * @param pickupTime
     * @return confirmation
     */
    public String placeOrderAtCounterAnonymous(List<ItemOrder> itemOrders, int shopId, LocalDateTime pickupTime){
        int orderId = shopCatalog.getShop(shopId).placeOrder(itemOrders, null, pickupTime, true);
        if (orderId == -1) {
            return "order cannot be placed";
        } else {
            return "order " + orderId + " successfully placed " ;
        }
    }

    /**
     * place order Online or at counter case when you're not an user
     * @param itemOrders
     * @param customerEmail
     * @param shopId
     * @param pickupTime
     * @param payOnline whether you payonline or pay at the counter
     * @return confirmation
     */
    public String placeOrderWithoutAccount(List<ItemOrder> itemOrders, String customerEmail, int shopId, LocalDateTime pickupTime, boolean payOnline) {
        Customer customer = customerCatalog.getCustomer(customerEmail);

        if (customer instanceof RegisteredCustomer) {
            return "Error: email " + customerEmail + " is already associated with a registered account";
        }

        String creationMsg = "";

        if (customer == null) {
            customer = new Customer(customerEmail);
            creationMsg += " and customer with id " + customer.getCustomerId() + " is created.";
            customerCatalog.addCustomer(customer);
        }
        int orderId = shopCatalog.getShop(shopId).placeOrder(itemOrders, customer, pickupTime, payOnline);
        if (orderId == -1) {
            return "order cannot be placed";
        } else {
            return "order " + orderId + " successfully placed " + creationMsg;
        }
    }

    /**
     * place order online or at the counter case when you are an user
     * @param itemOrders
     * @param customerId
     * @param shopId
     * @param pickupTime
     * @param payOnline whether you pay online or at the counter
     * @return confirmation
     */
    public String placeOrderWithAccount(List<ItemOrder> itemOrders, int customerId, int shopId, LocalDateTime pickupTime, boolean payOnline) {
        Customer customer = customerCatalog.getCustomer(customerId);

        if (customer == null) return "Invalid customer id";

        int orderId = shopCatalog.getShop(shopId)
                .placeOrder(itemOrders, customer, pickupTime, payOnline);
        if (orderId == -1) {
            return "order cannot be placed";
        } else {
            return "order " + orderId + " successfully placed ";
        }
    }


    //****** STATISTICS *******
    public Object computeStat(String statName, LocalDate beginDate, LocalDate endDate, Object... args) {
        return StatisticsCalculator.computeStatForCatalog(statName, beginDate, endDate, shopCatalog, args);
    }

    public Object computeStatForShop(String statName, LocalDate beginDate, LocalDate endDate, int shopId, Object... args) {
        Shop shop = shopCatalog.getShop(shopId);
        return StatisticsCalculator.computeStatForShop(statName, beginDate, endDate, shop, args);
    }
}