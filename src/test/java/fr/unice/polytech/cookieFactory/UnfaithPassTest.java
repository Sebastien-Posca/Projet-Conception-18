package fr.unice.polytech.cookieFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnfaithPassTest {

    UnfaithPass unfaithPass;

    @Before
    public void setUp(){
        unfaithPass = new UnfaithPass();
    }

    @Test
    public void createPassAccount(){
        int id = unfaithPass.createPassAccount();
        assertTrue( "the unfaith pass id must be > 0.", id > 0);
    }


    @Test
    public void getPointsOfPass_case_at_account_creation() {
        int id = unfaithPass.createPassAccount();

        assertEquals(0, unfaithPass.getPointsOfPass(id));
    }

    @Test
    public void getBalanceOfPass_case_at_account_creation() {
        int id = unfaithPass.createPassAccount();

        assertEquals(0, unfaithPass.getBalanceOfPass(id), 0.001);
    }

    @Test
    public void depositMoneyOnPass_case_below_limit() {

        int id = unfaithPass.createPassAccount();

        unfaithPass.depositMoneyOnPass(444444444, id, 30);
    }

    @Test
    public void getBalanceOfPass_case_after_deposit() {
        int id = unfaithPass.createPassAccount();

        int amount = 30;

        unfaithPass.depositMoneyOnPass(444444444, id, amount);

        assertEquals(amount, unfaithPass.getBalanceOfPass(id), 0.001);
    }


    @Test(expected = IllegalArgumentException.class)
    public void depositMoneyOnPass_case_above_limit() {

        int id = unfaithPass.createPassAccount();

        unfaithPass.depositMoneyOnPass(444444444, id, 60);
    }

    @Test
    public void givePointsToPass() {

        int id = unfaithPass.createPassAccount();

        unfaithPass.givePointsToPass(id, 60);
    }


    @Test
    public void getPointsOfPass_case_after_deposit() {

        int id = unfaithPass.createPassAccount();

        int points = 60;

        unfaithPass.givePointsToPass(id, points);

        assertEquals(points, unfaithPass.getPointsOfPass(id));
    }


    @Test
    public void pay_case_enough_money() {
        int id = unfaithPass.createPassAccount();

        unfaithPass.depositMoneyOnPass(444444444, id, 10);
        unfaithPass.pay(id, 5);
    }

    @Test
    public void getBalance_case_after_payment() {
        int id = unfaithPass.createPassAccount();

        unfaithPass.depositMoneyOnPass(444444444, id, 10);
        unfaithPass.pay(id, 5);
        assertEquals(5, unfaithPass.getBalanceOfPass(id), 0.001);
    }


    @Test(expected = IllegalArgumentException.class)
    public void pay_case_not_enough_money() {
        int id = unfaithPass.createPassAccount();

        unfaithPass.depositMoneyOnPass(444444444, id, 10);
        unfaithPass.pay(id, 15);
    }


    @Test
    public void use_points_case_enough_points() {
        int id = unfaithPass.createPassAccount();

        unfaithPass.givePointsToPass(id, 10);
        unfaithPass.usePoints(id, 5);
    }

    @Test
    public void getPointsOfPass_case_after_use() {

        int id = unfaithPass.createPassAccount();


        unfaithPass.givePointsToPass(id, 10);
        unfaithPass.usePoints(id, 5);

        assertEquals(5, unfaithPass.getPointsOfPass(id));
    }

}