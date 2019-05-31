package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeBuilder;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;
import fr.unice.polytech.cookieFactory.Shop.Customer;
import fr.unice.polytech.cookieFactory.Shop.ItemOrder;
import fr.unice.polytech.cookieFactory.Shop.Shop;
import fr.unice.polytech.cookieFactory.order.Order;
import fr.unice.polytech.cookieFactory.order.ProcessingState;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;
import org.junit.Before;
import org.junit.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ShopTest {

    Shop shop;
    CookieRecipe cookieRecipe;
    List<ItemOrder> itemOrderList;
    Customer customer;
    Supplier supplier;
    UnfaithPass unfaithPass;
    IngredientCatalog ingredientCatalog;
    CookieRecipeBuilder recipeBuilder;

    @Before
    public void setUp() {

        ingredientCatalog = IngredientCatalog.newDefaultCatalog();
        recipeBuilder = new CookieRecipeBuilder(ingredientCatalog);

        Map<Ingredient, Integer> priceTable = new HashMap<>();
        priceTable.put(ingredientCatalog.getIngredient("Plain dough"), 2);
        priceTable.put(ingredientCatalog.getIngredient("Vanilla"), 5);

        supplier = new Supplier("Supp", priceTable);
        customer = new Customer("test@gmail.com");

        cookieRecipe = recipeBuilder.build("Classic", "");
        List<CookieRecipe> availableRecipes = new ArrayList<>();
        availableRecipes.add(cookieRecipe);
        shop = new Shop("NY City", 0.10, availableRecipes, 1);
        shop.addIngredient(ingredientCatalog.getIngredient("Plain dough"), .20, supplier);
        shop.addIngredient(ingredientCatalog.getIngredient("Vanilla"), .50, supplier);
        itemOrderList = new ArrayList<ItemOrder>() {{
            add(new ItemOrder(cookieRecipe, 2));
        }};
        this.shop.addStock(ingredientCatalog.getIngredient("Plain dough"), 4);
        this.shop.addStock(ingredientCatalog.getIngredient("Vanilla"), 4);
        unfaithPass = new UnfaithPass();
        shop.setUnfaithPass(unfaithPass);
        customer.setUnfaithPassId(unfaithPass.createPassAccount());
        this.shop.addNewScheduleDay(LocalTime.of(8,30), LocalTime.of(18,0), LocalDate.of(2018,7,12));
        this.shop.addNewScheduleDay(LocalTime.of(8,30), LocalTime.of(18,0), LocalDate.of(2018,7,25));
        this.shop.addNewScheduleDay(LocalTime.of(8,00), LocalTime.of(17,0), LocalDate.of(2018,12,18));
        this.shop.addNewScheduleDay(LocalTime.of(8,00), LocalTime.of(17,0), LocalDate.now());
    }

    @Test
    public void item_orders_price() {
        double price = shop.getPrice(this.itemOrderList);
        // ((2*1.20+5*1.50)*2)*1.10*1 = 10.89
        assertEquals(21.78, price, 0.0001);
    }

    @Test
    public void add_recipe() {
        CookieRecipe cookieRecipe2 = recipeBuilder.build("Classic bis", "");
        shop.addRecipe(cookieRecipe2);
        assertTrue(this.shop.getAvailableRecipes().contains(cookieRecipe2));
    }

    @Test
    public void remove_recipe() {
        shop.removeRecipe(cookieRecipe);
        assertFalse(this.shop.getAvailableRecipes().contains(cookieRecipe));
    }

    @Test
    public void place_order() {
        LocalDateTime pickUp = LocalDateTime.parse("2018-12-18T10:15:30");
        this.shop.placeOrder(this.itemOrderList, this.customer, pickUp, true);
        assertTrue(this.shop.getOrders().size() != 0);
        assertSame(this.shop.getOrders().get(0).getItemOrders(), this.itemOrderList);
        assertSame(this.shop.getOrders().get(0).getCustomer(), this.customer);
        assertSame(this.shop.getOrders().get(0).getPickupTime(), pickUp);
    }

    @Test
    public void pay_order() {
        LocalDateTime pickUp = LocalDateTime.parse("2018-12-18T10:15:30");
        int orderId = this.shop.placeOrder(this.itemOrderList, this.customer, pickUp, true);
        this.shop.startProcessingOrder(orderId);
        this.shop.orderIsNowReady(orderId);
        this.shop.payOrderOnline(orderId, 12345);
        int pointsOfPass = this.unfaithPass.getPointsOfPass(customer.getUnfaithPassId());
        assertTrue(pointsOfPass > 0);
        this.shop.deliverOrder(orderId);

        //pay with pass case use money and don't get bonus means get points
        this.unfaithPass.depositMoneyOnPass(4848959,customer.getUnfaithPassId(),40);
        int orderId2 = this.shop.placeOrder(this.itemOrderList, this.customer, pickUp, true);
        this.shop.payOrderAtCounterWithPass(orderId2,true,false,customer.getUnfaithPassId());
        int pointsOfPass2 = this.unfaithPass.getPointsOfPass(customer.getUnfaithPassId());
        //you should have more points than before the order
        assertTrue(pointsOfPass2 - pointsOfPass > 0);

        //pay with pass case use points and get bonus means don't get points
        this.shop.addStock(ingredientCatalog.getIngredient("Plain dough"), 4);
        this.shop.addStock(ingredientCatalog.getIngredient("Vanilla"), 4);
        int orderId4 = this.shop.placeOrder(this.itemOrderList, this.customer, pickUp, true);
        this.shop.payOrderAtCounterWithPass(orderId4,false,true,customer.getUnfaithPassId());
        int pointsOfPass4 = this.unfaithPass.getPointsOfPass(customer.getUnfaithPassId());
        //you should have less points than before the order
        assertTrue(pointsOfPass4 - pointsOfPass2 < 0);
        //the last order created should be yours and the itemorder should be the same recipe with a count of 1
        Order lastOrder = this.shop.getDeliveredOrders().get(this.shop.getDeliveredOrders().size()-1);
        assertEquals(customer, lastOrder.getCustomer());
        assertEquals(itemOrderList.get(0).getCookieRecipe(), lastOrder.getItemOrders().get(0).getCookieRecipe());
        assertEquals(1,lastOrder.getItemOrders().get(0).getCount());

        //pay with pass case use points and get points means don't get bonus
        unfaithPass.givePointsToPass(customer.getUnfaithPassId(),19);
        int pointsOfPass3 = this.unfaithPass.getPointsOfPass(customer.getUnfaithPassId());
        this.shop.addStock(ingredientCatalog.getIngredient("Plain dough"), 4);
        this.shop.addStock(ingredientCatalog.getIngredient("Vanilla"), 4);
        int orderId3 = this.shop.placeOrder(this.itemOrderList, this.customer, pickUp, true);
        this.shop.payOrderAtCounterWithPass(orderId3,false,false, customer.getUnfaithPassId());
        int pointsOfPass5 = this.unfaithPass.getPointsOfPass(customer.getUnfaithPassId());
        //you should have less points than before the order
        assertTrue(pointsOfPass5 - pointsOfPass3 < 0);


    }

    @Test
    public void place_order_no_stock() {
        this.shop.removeStock(ingredientCatalog.getIngredient("Plain dough"), 4);
        this.shop.removeStock(ingredientCatalog.getIngredient("Vanilla"), 4);
        LocalDateTime pickUp = LocalDateTime.parse("2007-12-03T10:15:30");
        this.shop.placeOrder(this.itemOrderList, this.customer, pickUp, true);
        assertEquals(0, this.shop.getOrders().size());
    }

    @Test
    public void place_order_exceed_counter_limit() {
        this.shop.removeStock(ingredientCatalog.getIngredient("Plain dough"), 4);
        this.shop.removeStock(ingredientCatalog.getIngredient("Vanilla"), 4);
        LocalDateTime pickUp = LocalDateTime.parse("2007-12-03T10:15:30");
        this.shop.placeOrder(this.itemOrderList, this.customer, pickUp, false);
        assertTrue(shop.getPrice(itemOrderList) > 15);
        assertEquals(0, this.shop.getOrders().size());
    }

    @Test
    public void get_pending_orders() {
        assertTrue(this.shop.getPendingOrders().isEmpty());
        int id = this.shop.placeOrder(itemOrderList, this.customer, LocalDateTime.parse("2018-12-18T10:15:30"), true);
        assertFalse(this.shop.getPendingOrders().isEmpty());
    }

    @Test
    public void get_delivered_orders() {
        LocalDateTime pickUp = LocalDateTime.parse("2018-12-18T10:15:30");
        int id1 = this.shop.placeOrder(itemOrderList, this.customer, pickUp, true);
        int id2 = this.shop.placeOrder(itemOrderList, this.customer, pickUp, true);
        Order order = this.shop.getOrderById(id1);
        Order order2 = this.shop.getOrderById(id2);
        order.pay(574758545);
        order.startProcessingOrder();
        order.setReady();
        order.deliver();
        assertTrue(this.shop.getDeliveredOrders().contains(order));
        assertFalse(this.shop.getDeliveredOrders().contains(order2));
    }

    @Test
    public void refuse_order() {
        LocalDateTime pickUp = LocalDateTime.parse("2018-12-18T10:15:30");
        int id = this.shop.placeOrder(itemOrderList, this.customer, pickUp, true);
        this.shop.getOrderById(id).pay(574758545);
        shop.refuseOrder(id);
        assertSame(ProcessingState.REFUSED, shop.getOrderById(id).getProcessingState());
    }

    @Test
    public void order_too_big_for_pickupTime() { ;
        assertEquals(true, shop.isOrderAchievable(50, LocalDateTime.now().plusMinutes(10)));
        assertEquals(true, shop.isOrderAchievable(125, LocalDateTime.now().plusMinutes(30)));
        assertEquals(false, shop.isOrderAchievable(150, LocalDateTime.now().plusMinutes(25)));
    }

    @Test
    public void change_schedule_time() {
        LocalDate date = LocalDate.of(2012,12,12);

        this.shop.addNewScheduleDay(LocalTime.of(8,30), LocalTime.of(17,50), date);
        assertEquals("8h30, 17h50", shop.getShopSchedule().getScheduleIntervalOfDay(date).toString());

        this.shop.changeScheduleOfDay(LocalTime.of(10,30), LocalTime.of(16,35), date);
        assertEquals("10h30, 16h35", shop.getShopSchedule().getScheduleIntervalOfDay(date).toString());

        shop.placeOrder(itemOrderList, this.customer, LocalDateTime.parse("2012-12-12T15:15:30"), true);

        this.shop.changeScheduleOfDay(LocalTime.of(10,30), LocalTime.of(14,00), date);
        assertEquals("10h30, 16h35", shop.getShopSchedule().getScheduleIntervalOfDay(date).toString());


    }

    @Test
    public void conversionTest(){
        assertEquals(2.5,this.shop.getConvertedEuros(1),0);
        assertEquals(2,this.shop.getConvertedUnFaithPassPoints(5),0);
        this.shop.setConversionRate(0.25);
        assertEquals(4,this.shop.getConvertedEuros(1),0);
        assertEquals(1,this.shop.getConvertedUnFaithPassPoints(5),0);
    }

    @Test
    public void refuseOrderTest(){
        assertTrue(shop.getPrice(itemOrderList) > 15 );
        assertEquals(false, shop.isOrderPossible(itemOrderList,false));
        assertEquals(true, shop.isOrderPossible(itemOrderList,true));
        List<ItemOrder> itemOrderList2 = new ArrayList<>();
        itemOrderList2.add(new ItemOrder(cookieRecipe,1));
        assertTrue(shop.getPrice(itemOrderList2) < 15 );
        assertEquals(true, shop.isOrderPossible(itemOrderList2,true));
        assertEquals(true, shop.isOrderPossible(itemOrderList2,false));

    }

    @Test
    public void createItemOrderTest(){
        ItemOrder itemOrder = new ItemOrder(cookieRecipe,2);
        assertEquals(itemOrder.getCookieRecipe(), shop.createItemOrder(cookieRecipe,2).getCookieRecipe());
        assertEquals(2, shop.createItemOrder(cookieRecipe,2).getCount());

        CookieRecipe cookieRecipePerso = recipeBuilder.build("persoTest","plain dough, White chocolate topping, mixed, crunchy");
        assertNull(shop.createItemOrder(cookieRecipePerso,1));

        shop.addIngredient(ingredientCatalog.getIngredient("white chocolate topping"), .50, supplier);

        CookieRecipe cookieRecipePerso2 = recipeBuilder.build("persoTestZ","plain dough, White chocolate topping, mixed, crunchy");
        ItemOrder itemOrder2 = new ItemOrder(cookieRecipePerso2,1);
        assertEquals(itemOrder2.getCookieRecipe(),shop.createItemOrder(cookieRecipePerso2,1).getCookieRecipe());
        assertEquals(1, shop.createItemOrder(cookieRecipe,1).getCount());

    }

    @Test
    public void payAtCounterWithUnfaithPassTest(){

    }

}