package fr.unice.polytech.cookieFactory.Shop;

public class Customer {

    private static int nextId = 0;

    private int customerId;
    private String email;
    private int tempCCnumber=0;
    private int unfaithPassId = -1;


    /**
     * @param msg
     */
    public void notify(String msg) {
        System.out.println(msg);
    }

    /**
     * @param email
     */
    public Customer(String email) {
        this.email = email;
        this.customerId = nextId++;
    }

    public int getCustomerId() {
        return customerId;
    }

    /**
     *
     * @return a number < 0 if the customer does not have a unfaith pass id.
     */
    public int getUnfaithPassId() {
        return unfaithPassId;
    }

    public void setUnfaithPassId(int unfaithPassId) {
        this.unfaithPassId = unfaithPassId;
    }

    public String getEmail() {
        return email;
    }

    public void setTempCCnumb(int ccNumber){
        this.tempCCnumber = ccNumber;
    }
    public int getTempCCnumber(){
        return this.tempCCnumber;
    }

    public void removeTempCCnumb(){
        this.tempCCnumber = 0;
    }
}