package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.Shop.CustomerCatalog;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeBuilder;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;
import fr.unice.polytech.cookieFactory.Shop.Customer;
import fr.unice.polytech.cookieFactory.Shop.ItemOrder;
import fr.unice.polytech.cookieFactory.Shop.RegisteredCustomer;
import fr.unice.polytech.cookieFactory.Shop.Shop;
import fr.unice.polytech.cookieFactory.order.Order;
import fr.unice.polytech.cookieFactory.order.PaymentState;
import fr.unice.polytech.cookieFactory.order.ProcessingState;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrderTest {

    CookieRecipe cookieRecipe;
    IngredientCatalog ingredientCatalog;
    CookieRecipeBuilder cookieRecipeBuilder;
    List<ItemOrder> itemOrderList;
    Shop shop;
    Customer customer;
    Supplier supplier;

    @Before
    public void setUp() {

        ingredientCatalog = IngredientCatalog.newDefaultCatalog();

        cookieRecipeBuilder = new CookieRecipeBuilder(ingredientCatalog);

        Map<Ingredient, Integer> priceTable = new HashMap<>();
        priceTable.put(ingredientCatalog.getIngredient("Plain dough"), 2);
        priceTable.put(ingredientCatalog.getIngredient("Vanilla"), 5);
        priceTable.put(ingredientCatalog.getIngredient("white chocolate topping"), 3);

        supplier = new Supplier("Supp", priceTable);
        cookieRecipe = cookieRecipeBuilder.build("Classic", "");
        customer = new Customer("bernard@gmail.com");
        itemOrderList = new ArrayList<ItemOrder>() {{
            add(new ItemOrder(cookieRecipe, 2));
        }};

        shop = new Shop("NY City", 0.10,1);
        shop.addIngredient(ingredientCatalog.getIngredient("Plain dough"), .20, supplier);
        shop.addIngredient(ingredientCatalog.getIngredient("Vanilla"), .50, supplier);
        shop.addIngredient(ingredientCatalog.getIngredient("white chocolate topping"), .50, supplier);

    }

    @Test
    public void instantiation_case_no_discount() {
        Order order = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));
        // ((2*1.20+5*1.50)*2)*1.10*1 = 10.89
        assertEquals(21.78, order.getPrice(), 0.0001);
    }

    @Test
    public void instantiation_case_discount() {

        //creation of a customer with the fidelity program and 30 cookies bought
        RegisteredCustomer customer = new RegisteredCustomer("bernard@gmail.com", "password");
        customer.subscribeToFidelityProgram();

             customer.updateCookieCount(new ArrayList<ItemOrder>() {{
            add(new ItemOrder(cookieRecipe, 30));
        }});
        //

        Order order = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));
        //((2*1.20+5*1.50)*2)*1.10*0.9 = 10.89
        assertEquals(19.602, order.getPrice(), 0.0001);
    }

    @Test
    public void id_incrementation() {

        int id = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"))
                .getOrderId();

        assertEquals(id + 1,
                new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"))
                        .getOrderId());
    }


    @Test
    public void deliver_case_unready() {

        Order order = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));

        assertEquals(ProcessingState.PENDING, order.getProcessingState());

        order.deliver();

        assertEquals(ProcessingState.PENDING, order.getProcessingState());
    }

    @Test
    public void deliver_case_ready() {

        Order order = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));
        order.pay(574758545);
        assertEquals(ProcessingState.PENDING, order.getProcessingState());
        order.startProcessingOrder();
        order.setReady();

        assertEquals(ProcessingState.READY, order.getProcessingState());

        order.deliver();

        assertEquals(ProcessingState.DELIVERED, order.getProcessingState());
    }

    @Test
    public void pay_case_unpaid() {

        Order order = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));

        assertEquals(PaymentState.UNPAID, order.getPaymentState());

        order.pay(574758545);

        assertEquals(PaymentState.PAID, order.getPaymentState());
    }

    @Test
    public void deliver_case_unpaid() {

        Order order = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));
        order.startProcessingOrder();
        order.setReady();
        order.deliver();

        assertEquals(PaymentState.UNPAID, order.getPaymentState());
    }


    @Test
    public void deliver_case_paid() {

        Order order = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));

        assertEquals(PaymentState.UNPAID, order.getPaymentState());

        assertEquals(ProcessingState.PENDING, order.getProcessingState());

        order.pay(574758545);
        order.startProcessingOrder();
        order.setReady();

        assertEquals(PaymentState.PAID, order.getPaymentState());

        order.deliver();

        assertEquals(ProcessingState.DELIVERED, order.getProcessingState());
    }

    @Test
    public void cookie_incrementation() {
        RegisteredCustomer rc = new RegisteredCustomer("abcd", "pass");
        List<ItemOrder> listItemOrder = new ArrayList<>();
        listItemOrder.add(new ItemOrder(cookieRecipe, 24));

        assertEquals(0, rc.getCookieCount());

        Order order1 = new Order(listItemOrder, shop, rc, LocalDateTime.parse("2007-12-03T10:15:30"));
        order1.pay(574758545);

        assertEquals(24, rc.getCookieCount());
        assertFalse(rc.hasDiscount());

        Order order2 = new Order(listItemOrder, shop, rc, LocalDateTime.parse("2007-12-03T10:15:30"));
        order2.pay(574758545);

        assertEquals(0, rc.getCookieCount());
        assertTrue(rc.hasDiscount());
    }

    @Test
    public void test_elegibility() {
        RegisteredCustomer rc = new RegisteredCustomer("abcd", "pass");
        CustomerCatalog customerCatalog = new CustomerCatalog();
        customerCatalog.addCustomer(rc);

        List<ItemOrder> listItemOrder = new ArrayList<>();
        listItemOrder.add(new ItemOrder(cookieRecipe, 35));

        assertFalse(((RegisteredCustomer) customerCatalog.getCustomer(rc.getCustomerId())).hasDiscount());
        assertFalse(rc.hasDiscount());

        Order orderWithoutDiscount = new Order(listItemOrder, shop, customerCatalog.getCustomer(rc.getCustomerId()), LocalDateTime.parse("2007-12-03T10:15:30"));
        orderWithoutDiscount.pay(574758545);

        assertTrue(((RegisteredCustomer) customerCatalog.getCustomer(rc.getCustomerId())).hasDiscount());
        assertTrue(rc.hasDiscount());


        Order orderWithDiscount = new Order(listItemOrder, shop, customerCatalog.getCustomer(rc.getCustomerId()), LocalDateTime.parse("2007-12-03T10:15:30"));
        orderWithDiscount.pay(574758545);

        assertFalse(((RegisteredCustomer) customerCatalog.getCustomer(rc.getCustomerId())).hasDiscount());
        assertFalse(rc.hasDiscount());
    }

    @Test
    public void cancel_pending_order() {

        Order paidOrder = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));
        Order unpaidOrder = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));
        paidOrder.pay(574758545);

        assertEquals(ProcessingState.PENDING, paidOrder.getProcessingState());
        assertEquals(ProcessingState.PENDING, unpaidOrder.getProcessingState());

        paidOrder.cancelOrder();

        assertEquals(ProcessingState.CANCELED, paidOrder.getProcessingState());
        assertEquals(ProcessingState.PENDING, unpaidOrder.getProcessingState());

        unpaidOrder.cancelOrder();

        assertEquals(ProcessingState.CANCELED, unpaidOrder.getProcessingState());

        paidOrder.deliver();

        assertEquals(ProcessingState.CANCELED, paidOrder.getProcessingState());
    }

    @Test
    public void cancel_finished_order() {

        Order paidOrder = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));

        paidOrder.pay(574758545);
        assertEquals(ProcessingState.PENDING, paidOrder.getProcessingState());

        paidOrder.startProcessingOrder();
        assertEquals(ProcessingState.PROCESSING, paidOrder.getProcessingState());

        paidOrder.setReady();
        assertEquals(ProcessingState.READY, paidOrder.getProcessingState());

        paidOrder.cancelOrder();
        assertEquals(ProcessingState.READY, paidOrder.getProcessingState());

        paidOrder.deliver();
        assertEquals(ProcessingState.DELIVERED, paidOrder.getProcessingState());

        paidOrder.cancelOrder();
        assertEquals(ProcessingState.DELIVERED, paidOrder.getProcessingState());
    }

    @Test
    public void order_perso_test(){
        CookieRecipe cookieRecipePerso = cookieRecipeBuilder.build("persoTest","plain dough, White chocolate topping, mixed, crunchy");
        ItemOrder itemOrder = shop.createItemOrder(cookieRecipePerso,1);
        itemOrderList.add(itemOrder);
        System.out.println(itemOrder.isPerso());
        Order order = new Order(itemOrderList, shop, customer, LocalDateTime.parse("2007-12-03T10:15:30"));

        assertEquals(30.47, order.getPrice(), 0.0001);

    }
}