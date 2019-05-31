package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.Shop.RegisteredCustomer;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeBuilder;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;
import fr.unice.polytech.cookieFactory.Shop.Customer;
import fr.unice.polytech.cookieFactory.Shop.ItemOrder;
import fr.unice.polytech.cookieFactory.Shop.Shop;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StatisticsCalculatorTest {

    Shop shop;
    Customer customer;
    List<ItemOrder> itemOrderList;
    CookieRecipe cookieRecipe;
    Supplier supplier;
    IngredientCatalog ingredientCatalog;
    CookieRecipeBuilder recipeBuilder;

    int YEAR = 2018;
    LocalDate YEAR_START;
    LocalDate YEAR_END;

    @Before
    public void setUp() {

        ingredientCatalog = IngredientCatalog.newDefaultCatalog();
        recipeBuilder = new CookieRecipeBuilder(ingredientCatalog);

        Map<Ingredient, Integer> priceTable = new HashMap<>();
        priceTable.put(ingredientCatalog.getIngredient("Plain dough"), 2);
        priceTable.put(ingredientCatalog.getIngredient("Vanilla"), 5);

        supplier = new Supplier("Supp", priceTable);
        cookieRecipe = recipeBuilder.build("Classic", "");

        itemOrderList = new ArrayList<ItemOrder>() {{
            add(new ItemOrder(cookieRecipe, 1));
        }};

        customer = new Customer("bernard@gmail.com");

        shop = new Shop("NY City", 0.10,1);
        shop.addIngredient(ingredientCatalog.getIngredient("Plain dough"), .20, supplier);
        shop.addIngredient(ingredientCatalog.getIngredient("Vanilla"), .50, supplier);


        this.shop.addStock(ingredientCatalog.getIngredient("Plain dough"), 400);
        this.shop.addStock(ingredientCatalog.getIngredient("Vanilla"), 400);

        YEAR_START = LocalDate.of(YEAR, 1, 1);
        YEAR_END = LocalDate.of(YEAR + 1, 1, 1).minusDays(1);
        this.shop.addNewScheduleDay(LocalTime.of(8,30), LocalTime.of(18,0), LocalDate.of(2018,11,3)); // LocalDateTime.parse("2018-11-03T10:03:10")

    }

    @Test
    public void computePickUpTimeRepartition_case_no_archived_orders() {


        LocalDateTime currentDateTime = LocalDateTime.parse("2018-11-03T10:03:10");

        shop.placeOrder(itemOrderList, customer, currentDateTime.plusHours(2), true);


        Map<Integer, Float> hourToRepartition = StatisticsCalculator
                .computePickUpTimeRepartition(
                        currentDateTime.toLocalDate()
                        , currentDateTime.toLocalDate()
                        , shop.getDeliveredOrders()
                );

        assertTrue("hourToRepartition map is not empty", hourToRepartition.isEmpty());

    }


    @Test
    public void computePickUpTimeRepartition_case_one_delivered_order() {


        LocalDateTime currentDateTime = LocalDateTime.parse("2018-11-03T10:03:10").plusMinutes(25);

        int orderId = shop.placeOrder(itemOrderList, customer, currentDateTime.plusHours(2), true);


        shop.payOrderOnline(orderId, 444444444);
        shop.startProcessingOrder(orderId);
        shop.orderIsNowReady(orderId);
        shop.deliverOrder(orderId);

        Map<Integer, Float> hourToRepartition = StatisticsCalculator
                .computePickUpTimeRepartition(
                        currentDateTime.toLocalDate()
                        , currentDateTime.toLocalDate()
                        , shop.getDeliveredOrders()
                );


        assertFalse("recipeNameToRepartition map is empty", hourToRepartition.isEmpty());
        assertEquals(new Float(1), hourToRepartition.values().stream().findFirst().get());
    }

    @Test
    public void computePickUpTimeRepartition_case_equal_distribution() {

        //creates 24 orders with different picking hours / days
        for (int hour = 1; hour < 24; hour++) {

            Month randomMonth = Month.of((int) (Math.random() * 11) + 1);
            int randomDay = (int) (Math.random() * 27) + 1;

            LocalDateTime orderDateTime = LocalDateTime.of(YEAR, randomMonth, randomDay, hour, 0);

            this.shop.addNewScheduleDay(LocalTime.of(0,00), LocalTime.of(23,59), orderDateTime.toLocalDate());

            int orderId = shop.placeOrder(itemOrderList, customer, orderDateTime, true);


            shop.payOrderOnline(orderId, 444444444);
            shop.startProcessingOrder(orderId);
            shop.orderIsNowReady(orderId);
            shop.deliverOrder(orderId);
        }




        Map<Integer, Float> hourToRepartition = StatisticsCalculator
                                                .computePickUpTimeRepartition(YEAR_START, YEAR_END, shop.getDeliveredOrders());

        assertFalse("the hour to repartition map is empty", hourToRepartition.isEmpty());

        for(int hour = 1; hour < 24; hour++){
            assertTrue("the hour to repartition map does not contain the hour key " + hour,
                        hourToRepartition.containsKey(hour));
            assertEquals(1.0 / 24, hourToRepartition.get(hour), 0.01);
        }

    }

    @Test
    public void computeRecipeRepartition_case_no_delivered() {

        LocalDateTime currentDateTime =LocalDateTime.parse("2018-11-03T10:03:10");

        shop.placeOrder(itemOrderList, customer, currentDateTime.plusHours(2), true);


        Map<String, Float> recipeNameToRepartition = StatisticsCalculator
                .computeRecipeRepartition(
                        currentDateTime.toLocalDate()
                        , currentDateTime.toLocalDate()
                        , shop.getDeliveredOrders()
                        , new Object[]{ false }

                );

        assertTrue("hourToRepartition map is not empty", recipeNameToRepartition.isEmpty());

    }

    @Test
    public void computeRecipeRepartition_case_one_delivered_order_and_no_custom_recipes() {


        LocalDateTime currentDateTime = LocalDateTime.parse("2018-11-03T10:03:10");

        int orderId = shop.placeOrder(itemOrderList, customer, currentDateTime.plusHours(2), true);


        shop.payOrderOnline(orderId, 444444444);
        shop.startProcessingOrder(orderId);
        shop.orderIsNowReady(orderId);
        shop.deliverOrder(orderId);

        Map<String, Float> recipeNameToRepartition = StatisticsCalculator
                .computeRecipeRepartition(
                        currentDateTime.toLocalDate()
                        , currentDateTime.toLocalDate()
                        , shop.getDeliveredOrders()
                        , new Object[]{ false }

                );


        assertFalse("recipeNameToRepartition map is empty", recipeNameToRepartition.isEmpty());
        assertEquals(new Float(1), recipeNameToRepartition.values().stream().findFirst().get());
    }


    @Test
    public void computeRecipeRepartition_case_equal_repartition_and_no_custom_recipes() {

        //CREATION OF TWO ORDERS
        LocalDateTime currentDateTime = LocalDateTime.parse("2018-11-03T10:03:10");


        List<ItemOrder> itemOrderList1 = new ArrayList<ItemOrder>() {{
            add(new ItemOrder(recipeBuilder.build("Recette 1", ""), 1));
            add(new ItemOrder(recipeBuilder.build("Recette 2", ""), 1));
        }};


        List<ItemOrder> itemOrderList2 = new ArrayList<ItemOrder>() {{
            add(new ItemOrder(recipeBuilder.build("Recette 3", ""), 1));
        }};



        int orderId = shop.placeOrder(itemOrderList1, customer, currentDateTime.plusHours(2), true);

        shop.payOrderOnline(orderId, 444444444);
        shop.startProcessingOrder(orderId);
        shop.orderIsNowReady(orderId);
        shop.deliverOrder(orderId);

        orderId = shop.placeOrder(itemOrderList2, customer, currentDateTime.plusHours(2), true);

        shop.payOrderOnline(orderId, 444444444);
        shop.startProcessingOrder(orderId);
        shop.orderIsNowReady(orderId);
        shop.deliverOrder(orderId);

        //////////////////////////////

        Map<String, Float> recipeNameToRepartition = StatisticsCalculator
                                                    .computeRecipeRepartition(
                                                            currentDateTime.toLocalDate()
                                                            , currentDateTime.toLocalDate()
                                                            , shop.getDeliveredOrders()
                                                            , new Object[]{ false }
                                                    );

        for(String recipe : new String[] { "Recette 1", "Recette 2", "Recette 3"}){
            assertTrue("the recipe to repartition map does not contain recipe " + recipe,
                        recipeNameToRepartition.containsKey(recipe));
            assertEquals(0.33, recipeNameToRepartition.get(recipe), 0.01);
        }

    }

    @Test
    public void computeRecipeRepartition_case_not_equal_repartition_and_no_custom_recipes() {

        //CREATION OF TWO ORDERS
        LocalDateTime currentDateTime = LocalDateTime.parse("2018-11-03T10:03:10");


        List<ItemOrder> itemOrderList1 = new ArrayList<ItemOrder>() {{
            add(new ItemOrder(recipeBuilder.build("Recette 1", ""), 2));
            add(new ItemOrder(recipeBuilder.build("Recette 2", ""), 1));
        }};


        List<ItemOrder> itemOrderList2 = new ArrayList<ItemOrder>() {{
            add(new ItemOrder(recipeBuilder.build("Recette 3", ""), 1));
        }};



        int orderId = shop.placeOrder(itemOrderList1, customer, currentDateTime.plusHours(2), true);

        shop.payOrderOnline(orderId, 444444444);
        shop.startProcessingOrder(orderId);
        shop.orderIsNowReady(orderId);
        shop.deliverOrder(orderId);

        orderId = shop.placeOrder(itemOrderList2, customer, currentDateTime.plusHours(2), true);

        shop.payOrderOnline(orderId, 444444444);
        shop.startProcessingOrder(orderId);
        shop.orderIsNowReady(orderId);
        shop.deliverOrder(orderId);

        //////////////////////////////

        Map<String, Float> recipeNameToRepartition = StatisticsCalculator
                .computeRecipeRepartition(
                        currentDateTime.toLocalDate()
                        , currentDateTime.toLocalDate()
                        , shop.getDeliveredOrders()
                        , new Object[]{ false }
                );

        assertTrue("the recipe to repartition map does not contain Recette 1",
                recipeNameToRepartition.containsKey("Recette 1"));
        assertEquals(0.50, recipeNameToRepartition.get("Recette 1"), 0.01);


        for(String recipe : new String[] {"Recette 2", "Recette 3"}){
            assertTrue("the recipe to repartition map does not contain recipe " + recipe,
                    recipeNameToRepartition.containsKey(recipe));
            assertEquals(0.25, recipeNameToRepartition.get(recipe), 0.01);
        }

    }

    @Test
    public void computeAverageDeliveredCookieNumber_case_no_deliveries() {

        LocalDateTime dateTime =LocalDateTime.parse("2018-11-03T10:03:10");

        int LAST_DAY_OFFSET = 3;

        int average_delivered_cookie_number =   StatisticsCalculator
                .computeAverageDeliveredCookieNumber(
                        dateTime.toLocalDate(),
                        dateTime.plusDays(LAST_DAY_OFFSET).toLocalDate(),
                        shop.getDeliveredOrders(),
                        null
                );
        assertEquals(0, average_delivered_cookie_number);
    }


    @Test
    public void computeAverageDeliveredCookieNumber_case_equal_deliveries() {

        LocalDateTime dateTime = LocalDateTime.parse("2018-11-03T10:03:10").plusMinutes(25);

        int LAST_DAY_OFFSET = 3;

        for(int day_offset = 0; day_offset < LAST_DAY_OFFSET; day_offset++){
            this.shop.addNewScheduleDay(LocalTime.of(8,30), LocalTime.of(18,0), dateTime.plusDays(day_offset).toLocalDate());
            int orderId = shop.placeOrder(itemOrderList, customer, dateTime.plusDays(day_offset), true);

            shop.payOrderOnline(orderId, 444444444);
            shop.startProcessingOrder(orderId);
            shop.orderIsNowReady(orderId);
            shop.deliverOrder(orderId);

        }

        int average_delivered_cookie_number =   StatisticsCalculator
                                                .computeAverageDeliveredCookieNumber(
                                                        dateTime.toLocalDate(),
                                                        dateTime.plusDays(LAST_DAY_OFFSET).toLocalDate(),
                                                        shop.getDeliveredOrders(),
                                                        null
                                                );
        assertEquals(1, average_delivered_cookie_number);
    }


    @Test
    public void computeStatForShop(){

        LocalDateTime dateTime = LocalDateTime.parse("2018-11-03T10:03:10");

        Object object = StatisticsCalculator.computeStatForShop("AverageDeliveredCookieNumber", dateTime.toLocalDate(),
                                                                dateTime.toLocalDate(), shop);
        assertTrue(object.toString(), object instanceof Integer);
        assertEquals(0, (int)object);
    }


    @Test
    public void computeStatForCatalog(){
        RegisteredCustomer.ShopCatalog shopCatalog = new RegisteredCustomer.ShopCatalog();
        shopCatalog.addShop(shop, new UnfaithPass());

        LocalDateTime dateTime = LocalDateTime.parse("2018-11-03T10:03:10");

        Object object = StatisticsCalculator.computeStatForCatalog("AverageDeliveredCookieNumber", dateTime.toLocalDate(),
                dateTime.toLocalDate(), shopCatalog);
        assertTrue(object.toString(), object instanceof Integer);
        assertEquals(0, (int)object);
    }

}