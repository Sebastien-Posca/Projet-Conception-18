package fr.unice.polytech.cookieFactory.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerCatalog {


    private List<Customer> customerList;

    public CustomerCatalog() {
        customerList = new ArrayList<>();
    }

    public Customer addCustomer(Customer customer) {

        for (Customer currentCustomer : customerList) {

            if (currentCustomer.getEmail().equals(customer.getEmail()))
                return currentCustomer;
        }

        customerList.add(customer);
        return customer;
    }

    public String bindUnfaithPassId(int unfaithPassId, int customerId) {

        Customer customer = getCustomerByUnfaithPassId(unfaithPassId);
        if(customer != null)
            return "unfaith pass " + unfaithPassId + " already binded with customer " + customerId;

        customer = getCustomer(customerId);
        if(customer == null)
            return "customer " + customerId + " does not exist";

        customer.setUnfaithPassId(unfaithPassId);
        return "successful binding between unfaith pass " + unfaithPassId + " and customer " + customerId;
    }

    public Customer getCustomer(int customerId) {
        List<Customer> foundCustomers = customerList.stream()
                .filter(customer -> customer.getCustomerId() == customerId)
                .collect(Collectors.toList());
        if (foundCustomers.isEmpty())
            return null;
        return foundCustomers.get(0);
    }

    public Customer getCustomer(String emailAddress) {
        List<Customer> foundCustomers = customerList.stream()
                .filter(customer -> customer.getEmail().equals(emailAddress))
                .collect(Collectors.toList());
        if (foundCustomers.isEmpty())
            return null;
        return foundCustomers.get(0);
    }

    public Customer getCustomerByUnfaithPassId(int unfaithPassId){
        List<Customer> foundCustomers = customerList.stream()
                .filter(customer -> customer.getUnfaithPassId() == unfaithPassId)
                .collect(Collectors.toList());
        if (foundCustomers.isEmpty())
            return null;
        return foundCustomers.get(0);
    }

}
