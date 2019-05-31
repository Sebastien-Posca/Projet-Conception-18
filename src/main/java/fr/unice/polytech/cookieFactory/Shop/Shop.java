package fr.unice.polytech.cookieFactory.Shop;

import fr.unice.polytech.cookieFactory.Supplier;
import fr.unice.polytech.cookieFactory.UnfaithPass;
import fr.unice.polytech.cookieFactory.order.Order;
import fr.unice.polytech.cookieFactory.order.ProcessingState;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;
import fr.unice.polytech.cookieFactory.utils.InvalidIdException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;


public class Shop {

    private List<Order> orders = new ArrayList<>();
    private List<CookieRecipe> availableRecipes = new ArrayList<>();
    private List<ShopIngredient> availableIngredient = new ArrayList<>();
    private int shopId;
    private String location;
    private double taxeRate;
    private double customRecipeMargin;
    private CookieRecipe cookieRecipeOfTheMonth;
    private double conversionRate = 0.4; // 1 points vaut 2.5 euros  manager should modify this
    private final int COUNTER_LIMIT = 15;
    private double unfaithPointEarnedRatio = 0.2; // earn 1 points every 5 euro spend
    private UnfaithPass unfaithPass;

    private Schedule shopSchedule;

    private int next_shopId = 0;

    /**
     * @param location
     * @param taxeRate
     */
    public Shop(String location, double taxeRate, double customRecipeMargin) {
        this.location = location;
        this.taxeRate = taxeRate;
        this.shopId = next_shopId++;
        this.customRecipeMargin = customRecipeMargin;
        shopSchedule = new Schedule();
    }

    public Shop(String location, double taxeRate, List<CookieRecipe> availableRecipes, double customRecipeMargin) {
        this.location = location;
        this.taxeRate = taxeRate;
        this.customRecipeMargin = customRecipeMargin;
        this.shopId = this.location.hashCode();
        this.availableRecipes = availableRecipes;
        shopSchedule = new Schedule();
    }

    public void setUnfaithPass(UnfaithPass unfaithPass) {
        this.unfaithPass = unfaithPass;
    }

    public int getShopId() {
        return shopId;
    }

    public String getLocation() {
        return location;
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public Order getOrderById(int id) {
        return orders.stream().filter(order -> order.getOrderId() == id).collect(Collectors.toList()).get(0);
    }

    public List<CookieRecipe> getAvailableRecipes() {
        return availableRecipes;
    }

    public ShopIngredient getShopIngredientByIngredient(Ingredient ingredient){

        return this.availableIngredient.stream().filter(i-> i.getIngredient().equals(ingredient))
                .collect(Collectors.toList()).get(0);
    }

    /**
     * @param cookieRecipe
     */
    public boolean addRecipe(CookieRecipe cookieRecipe) {
        return this.availableRecipes.add(cookieRecipe);
    }

    public void addIngredient(Ingredient ingredient, double margin, Supplier supplier) {
        for (ShopIngredient shIg : availableIngredient) {
            if (ingredient.equals(shIg.getIngredient()))
                return;
        }
        this.availableIngredient.add(new ShopIngredient(this, ingredient, margin, supplier, 0));
    }

    public CookieRecipe getRecipe(String Name){
        return availableRecipes.stream().filter(recipe -> recipe.getName().equals(Name)).collect(Collectors.toList()).get(0);
    }

    public void setIngredientMargin(Ingredient ingredient, double margin) {
        for (ShopIngredient shopIngredient : availableIngredient) {
            if (ingredient.equals(shopIngredient.getIngredient())) {
                shopIngredient.setMargin(margin);
                return;
            }
        }
    }

    public void setIngredientSupplier(Ingredient ingredient, Supplier supplier) {
        for (ShopIngredient shopIngredient : availableIngredient) {
            if (ingredient.equals(shopIngredient.getIngredient())) {
                shopIngredient.setSupplier(supplier);
                return;
            }
        }
    }

    public void setRecipeOfTheMonth(CookieRecipe cookieRecipe){
        this.availableRecipes.remove(cookieRecipeOfTheMonth);
        this.cookieRecipeOfTheMonth = cookieRecipe;
        this.availableRecipes.add(cookieRecipe);
    }

    /**
     * @param taxeRate : must be ranged between 0 (incl) and 1 (incl)
     */
    public void setTaxeRate(double taxeRate) {
        if (taxeRate < 0 || taxeRate > 1) {
            throw new IllegalArgumentException("the shop taxe rate must be ranged between 0 (incl) and 1 (incl)");
        }
        this.taxeRate = taxeRate;
    }

    public void addStock(Ingredient ingredient, int stock){
        this.getShopIngredientByIngredient(ingredient).addStock(stock);
    }

    public void removeStock(Ingredient ingredient, int stock){
        this.getShopIngredientByIngredient(ingredient).removeStock(stock);
    }


    public boolean validatePersonnalisedCookie(List<Ingredient> ingredients){
        int n = 0;
        for(Ingredient ingredient : ingredients){
            for(ShopIngredient shopIngredient : availableIngredient){
                if(shopIngredient.getIngredient().equals(ingredient)){
                    n++;
                }
            }
        }
        return n == ingredients.size();
    }



    /**
     * @param recipe
     */
    public boolean removeRecipe(CookieRecipe recipe) {
        return this.availableRecipes.remove(recipe);
    }

    /**
     * @param orderId
     */
    public void refuseOrder(int orderId) {
        this.getOrderById(orderId).refuse();
    }

    /**
     * @param orderId
     */
    public void deliverOrder(int orderId) {
        this.getOrderById(orderId).deliver();
    }

    /**
     * @param orderId
     * @param creditCardNumber
     */
    public void payOrderOnline(int orderId, int creditCardNumber) {
        Order order = this.getOrderById(orderId);
        int unfaithPassId = order.getCustomer().getUnfaithPassId();
        order.pay(creditCardNumber);
        if(unfaithPassId != -1){
            unfaithPass.givePointsToPass(unfaithPassId, (int)(unfaithPointEarnedRatio*order.getPrice()));
        }
    }

    public void payOrderAtCounterWithCreditCard(int orderId, int creditCardNumber){
        Order order = getOrderById(orderId);

        //customer null => cookies have already been made
        if(order.getCustomer() == null){
            startProcessingOrder(orderId);
            orderIsNowReady(orderId);
            this.getOrderById(orderId).pay(creditCardNumber);
            deliverOrder(orderId);
        }
    }

    public void payOrderAtCounterWithCash(int orderId){
        Order order = getOrderById(orderId);
        //customer null => cookies have already been made
        if(order.getCustomer() == null){
            startProcessingOrder(orderId);
            orderIsNowReady(orderId);
            this.getOrderById(orderId).payWithCash();
            deliverOrder(orderId);
        }
    }

    /**
     * pay the order at counter using your unFaithPass
     * @param orderId
     * @param useMoney whether you pay with the money on your card or the points
     * @param getBonus whether you want the bonus or get the points
     * @param unfaithPassId
     */
    public void payOrderAtCounterWithPass(int orderId, boolean useMoney, boolean getBonus, int unfaithPassId) {
        Order order = this.getOrderById(orderId);
        if (unfaithPassId == -1) {
            throw new InvalidIdException(unfaithPassId, "unfaith pass");
        }
        if (useMoney) {
            unfaithPass.pay(unfaithPassId, order.getPrice());
        } else { //usePoints
            unfaithPass.usePoints(unfaithPassId, getConvertedUnFaithPassPoints(order.getPrice()));
        }
        if (!getBonus) {
            unfaithPass.givePointsToPass(unfaithPassId, (int) (unfaithPointEarnedRatio * order.getPrice()));
        } else { //Bonus = le cookie le moins cher en cadeau (en plus)
            ItemOrder minItemOrder = order.getItemOrders().stream().min(
                    Comparator.comparing(i -> getPrice(new ArrayList<ItemOrder>(Arrays.asList(i)))))
                    .orElseThrow(NoSuchElementException::new);
            ItemOrder itemOrder = new ItemOrder(minItemOrder.getCookieRecipe(),1);
            int orderBonusId = placeOrder(new ArrayList<ItemOrder>(Arrays.asList(itemOrder)), order.getCustomer(), order.getPickupTime(), true);
            startProcessingOrder(orderBonusId);
            orderIsNowReady(orderBonusId);
            this.getOrderById(orderBonusId).setPaid();
            deliverOrder(orderBonusId);
        }

        startProcessingOrder(orderId);
        orderIsNowReady(orderId);
        this.getOrderById(orderId).setPaid();
    }

    /**
     * @param cookieCount
     * @return the price of the sum of the ItemOrders including taxes
     */
    public double getPrice(List<ItemOrder> cookieCount) {
        int itemPerso = 0;
        double price = 0.0;
        for (ItemOrder itemOrder : cookieCount) {
            if (itemOrder.isPerso()){itemPerso+=itemOrder.getCount();}
            CookieRecipe cr = itemOrder.getCookieRecipe();
            for (Ingredient ingredient : cr.getIngredients()) {
                for (ShopIngredient shIg : this.availableIngredient) {
                    if (ingredient.equals(shIg.getIngredient())) {
                        price += shIg.computePrice() * itemOrder.getCount();
                    }
                }
            }
        }
        return (price+(itemPerso)* customRecipeMargin) * (1 + taxeRate);

    }

    public List<Order> getPendingOrders() {
        return orders.stream().filter(order -> order.getProcessingState() == ProcessingState.PENDING).collect(Collectors.toList());
    }

    public List<Order> getDeliveredOrders() {
        return orders.stream().filter(order -> order.getProcessingState() == ProcessingState.DELIVERED).collect(Collectors.toList());
    }

    /**
     * place any order
     * @param itemOrders
     * @param customer
     * @param pickUpTime
     * @param counterPrice whether you pay at the counter
     * @return the id of the order or -1 if an error occured
     */
    public int placeOrder(List<ItemOrder> itemOrders, Customer customer, LocalDateTime pickUpTime, boolean counterPrice) {

        int cookieCount = 0;
        for (ItemOrder itemOrder: itemOrders) {
            cookieCount += itemOrder.getCount();
        }
        try {
            if(this.isOrderPossible(itemOrders,counterPrice) && this.checkIfValidPickUpTime(pickUpTime) && isOrderAchievable(cookieCount, pickUpTime)) {
                Order order = new Order(itemOrders, this, customer, pickUpTime);
                this.orders.add(order);
                this.getShopIngredientItemCountMap(itemOrders).keySet()
                        .forEach(s-> removeStock(s.getIngredient(),this.getShopIngredientItemCountMap(itemOrders).get(s)));
                return order.getOrderId();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
        return -1;

    }

    /**
     * if the order is possible with the schedule
     * @param cookieCount
     * @param pickupTime
     * @return true if possible
     */
    public boolean isOrderAchievable(int cookieCount, LocalDateTime pickupTime) {
        return cookieCount <= 100 || (!(LocalTime.now().until(pickupTime, MINUTES) < 0.20 * cookieCount));
    }

    private boolean checkIfValidPickUpTime(LocalDateTime pickUpTime)  {
        return this.shopSchedule.isDuringSchedule(pickUpTime.getHour(), pickUpTime.getMinute(), pickUpTime.toLocalDate());
    }

    /**
     * create an item order by making sure the shop can make it
     * @param cr
     * @param count
     * @return
     */
    public ItemOrder createItemOrder(CookieRecipe cr, int count){
        if(!availableRecipes.contains(cr)){
            if(!validatePersonnalisedCookie(cr.getIngredients())){
                return null;
            }
            ItemOrder itemOrder = new ItemOrder(cr,count);
            itemOrder.setPerso(true);
            return itemOrder;

        }
        return new ItemOrder(cr,count);
    }

    /**
     * prevent paying at counter a too expensive order
     * and checks if there is enough stock
     * @param itemOrders
     * @param counterPrice whether you want to pay at counter
     * @return
     */

    public boolean isOrderPossible(List<ItemOrder> itemOrders, boolean counterPrice) {
        if (!counterPrice && getPrice(itemOrders) > COUNTER_LIMIT) {
            return false;
        }
        for(ShopIngredient shopIngredient : this.getShopIngredientItemCountMap(itemOrders).keySet()){
            if (shopIngredient.getStock() - this.getShopIngredientItemCountMap(itemOrders).get(shopIngredient) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * calculate the count needed for each shopIngredient necessary to make the itemOrders recipes
     * @param itemOrders
     * @return a map of <shopIngredient, count needed>
     */
    public Map<ShopIngredient, Integer> getShopIngredientItemCountMap(List<ItemOrder> itemOrders){
        Map<ShopIngredient, Integer> shopIngredients  = new HashMap();
        for (ItemOrder itemOrder : itemOrders) {
            CookieRecipe cr = itemOrder.getCookieRecipe();
            for (Ingredient ingredient : cr.getIngredients()){
                shopIngredients.put(this.getShopIngredientByIngredient(ingredient), itemOrder.getCount());
            }
        }
        return shopIngredients;
    }

    public boolean orderIsNowReady(int orderId) {
       return this.getOrderById(orderId).setReady();
    }

    public boolean cancelOrder(int orderId) {
        for (ItemOrder itemOrder : this.getOrderById(orderId).getItemOrders()) {
            CookieRecipe cr = itemOrder.getCookieRecipe();
            for (Ingredient ingredient : cr.getIngredients()) {
                for (ShopIngredient shIg : this.availableIngredient) {
                    if (ingredient.equals(shIg.getIngredient())) {
                        addStock(ingredient, itemOrder.getCount());
                    }
                }
            }
        }
        return this.getOrderById(orderId).cancelOrder();
    }

    public boolean startProcessingOrder(int orderId) {
        return this.getOrderById(orderId).startProcessingOrder();
    }

    public void setCustomRecipeMargin(double customRecipeMargin){
        this.customRecipeMargin = customRecipeMargin;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public double getConvertedEuros(int unFaithPassPoints){
        return unFaithPassPoints/conversionRate;
    }

    public int getConvertedUnFaithPassPoints(double euros){
        return (int) ((int)euros*conversionRate);
    }

    public void setUnfaithPointEarnedRatio(double newRatio){
        this.unfaithPointEarnedRatio = newRatio;
    }

    public double getUnfaithPointEarnedRatio() {

        return unfaithPointEarnedRatio;
    }

    public Schedule getShopSchedule() {
        return this.shopSchedule;
    }

    public double getCustomRecipeMargin(){
        return this.customRecipeMargin;
    }

    public void addNewScheduleDay(LocalTime openingTime, LocalTime closingTime, LocalDate newDate) {
        try {
            this.shopSchedule.addScheduleOfDay(openingTime, closingTime, newDate);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void changeScheduleOfDay(LocalTime openingTime, LocalTime closingTime, LocalDate day) {
        for(Order order : orders) {
            if(this.shopSchedule.getSchedule().containsKey(day)) {
                LocalDateTime pickupTime = order.getPickupTime();
                if(pickupTime.toLocalDate().isEqual(day) && (pickupTime.toLocalTime().isAfter(closingTime)  || pickupTime.toLocalTime().isBefore(openingTime))){
                    //doNothing
                    return;
                }
            }
        }
        shopSchedule.changeScheduleOfDay(openingTime, closingTime, day);
    }
}