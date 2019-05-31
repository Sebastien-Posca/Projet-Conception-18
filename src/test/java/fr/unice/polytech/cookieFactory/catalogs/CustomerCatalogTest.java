package fr.unice.polytech.cookieFactory.catalogs;

import fr.unice.polytech.cookieFactory.Shop.Customer;
import fr.unice.polytech.cookieFactory.Shop.CustomerCatalog;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class CustomerCatalogTest {

    CustomerCatalog customerCatalog = new CustomerCatalog();
    
    @Test
    public void addCustomer() {
        Customer customer = new Customer("bernard@gmail.com");
        Customer sameCustomer = new Customer("bernard@gmail.com");

        //checks that the customer is not duplicated
        customerCatalog.addCustomer(customer);
        assertSame(customer, customerCatalog.addCustomer(sameCustomer));
    }

    @Test
    public void getCustomer_case_by_id() {
        Customer customer = new Customer("test@gmail.com");

        customerCatalog.addCustomer(customer);
        assertSame(customer, customerCatalog.getCustomer(customer.getCustomerId()));
    }

    @Test
    public void getCustomer_case_by_email() {
        Customer customer = new Customer("test2@gmail.com");

        customerCatalog.addCustomer(customer);
        assertSame(customer, customerCatalog.getCustomer(customer.getEmail()));
    }
}