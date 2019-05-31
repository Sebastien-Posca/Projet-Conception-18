package fr.unice.polytech.cookieFactory.order;

import fr.unice.polytech.cookieFactory.Shop.Customer;
import fr.unice.polytech.cookieFactory.Shop.ItemOrder;
import fr.unice.polytech.cookieFactory.Shop.RegisteredCustomer;
import fr.unice.polytech.cookieFactory.Shop.Shop;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private static int nextOrderId = 0;

    private ProcessingState processingState;
    private PaymentState paymentState;
    private List<ItemOrder> itemOrders;
    private LocalDateTime pickupTime;
    private Customer customer;
    private int orderId;
    private double price;
    private boolean isDiscounted = false;

    /**
     * create an order
     * apply discount
     * @param itemOrders
     * @param shop
     * @param customer
     * @param pickupTime
     */
    public Order(List<ItemOrder> itemOrders, Shop shop, Customer customer, LocalDateTime pickupTime) {
        this.itemOrders = itemOrders;
        this.customer = customer;
        this.processingState = ProcessingState.PENDING;
        this.paymentState = PaymentState.UNPAID;
        this.pickupTime = pickupTime;

        price = shop.getPrice(itemOrders);
        if (customer instanceof RegisteredCustomer && ((RegisteredCustomer) customer).hasDiscount()) {
            ((RegisteredCustomer) customer).useDiscount();
            price *= 0.9;
            isDiscounted = true;
        }

        this.orderId = nextOrderId++;
    }

    public List<ItemOrder> getItemOrders() {
        return itemOrders;
    }

    public LocalDateTime getPickupTime() {
        return pickupTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderId() {
        return orderId;
    }

    public ProcessingState getProcessingState() {
        return processingState;
    }

    public PaymentState getPaymentState() {
        return this.paymentState;
    }

    /**
     * refuse an oder that has been paid because it's not doable anymore
     * @return confirmation of refusing
     */
    public boolean refuse() {
        if (paymentState == PaymentState.PAID) {
            processingState = ProcessingState.REFUSED;

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("We can't deliver your order ");
            strBuilder.append(orderId);
            strBuilder.append(" at the given time : ");
            strBuilder.append(pickupTime.toString());
            strBuilder.append(", please contact us to reschedule.");

            customer.notify(strBuilder.toString());

            return true;
        }
        return false;
    }

    public boolean deliver() {
        if (paymentState == PaymentState.PAID && processingState == ProcessingState.READY) {
            processingState = ProcessingState.DELIVERED;
            return true;
        }
        return false;
    }

    public double getPrice() {
        return price;
    }

    public boolean isDiscounted() {
        return isDiscounted;
    }

    public boolean payWithCash(){
        if (this.paymentState == PaymentState.UNPAID) {
            setPaid();
            return true;
        }
        return false;
    }

    public boolean pay(int creditCardNumber) {
        if (this.paymentState == PaymentState.UNPAID) {
        	customer.setTempCCnumb(creditCardNumber);

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("Your order ");
            strBuilder.append(orderId);
            strBuilder.append("has been successfully paid. Order contents :\n");
            strBuilder.append(toString());

            customer.notify(strBuilder.toString());
            setPaid();

            if (!isDiscounted() && customer instanceof RegisteredCustomer) {
                ((RegisteredCustomer) customer).updateCookieCount(this.getItemOrders());
            }
            return true;
        }
        return false;
    }

    public String refund(){
            return "Customer " + customer.getEmail() + " got refund";
	}

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        itemOrders.forEach(subOrder -> {
            strBuilder.append(subOrder.toString());
            strBuilder.append('\n');
        });
        strBuilder.append("Total price : ");
        strBuilder.append(getPrice());
        if (isDiscounted) strBuilder.append(" [Discounted]");

        return strBuilder.toString();
    }

    public void setPaid(){
        paymentState = PaymentState.PAID;
    }

    public boolean setReady() {
        if (this.processingState == ProcessingState.PROCESSING) {
			this.processingState = ProcessingState.READY;
            if(customer != null) customer.notify("your order is ready");
			return true;
		}
        return false;
    }

    public boolean cancelOrder() {
        if (this.processingState == ProcessingState.PENDING) {
            this.processingState = ProcessingState.CANCELED;
			refund();
			return true;
        } else {
            return false;
        }
    }

    public boolean startProcessingOrder() {
        if (this.processingState == ProcessingState.PENDING) {
            this.processingState = ProcessingState.PROCESSING;
			customer.removeTempCCnumb();
			return true;
		}
		return false;
    }
}