package fr.unice.polytech.cookieFactory.order;

/**
 * enum of states of an order
 */
public enum ProcessingState {
    PENDING, PROCESSING, READY, DELIVERED, REFUSED, CANCELED;
}