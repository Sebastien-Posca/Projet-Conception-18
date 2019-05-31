package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.utils.InvalidIdException;

import java.util.HashMap;
import java.util.Map;

public class UnfaithPass {

    private Map<Integer,Integer> mapOfPoints = new HashMap<>();
    private Map<Integer,Double> mapOfMoney = new HashMap<>();

    private static int MAX_BALANCE = 50;

    public void depositMoneyOnPass(int creditCardNumber, int unfaithPassID, float moneyToDeposit){

        checkId(mapOfMoney, unfaithPassID);
        double balance = mapOfMoney.get(unfaithPassID);

        if(moneyToDeposit + balance > MAX_BALANCE){
            throw new IllegalArgumentException(
                    "The maximum authorized balance is " + MAX_BALANCE
                    + ": you can't deposit " + moneyToDeposit + " in a balance of " +balance + "."
            );
        }

        mapOfMoney.put(unfaithPassID, balance + moneyToDeposit);
    }

    /**
     * try to pay with money on the pass
     * @param unfaithPassID
     * @param amountToPay
     */
    public void pay(int unfaithPassID, double amountToPay){

        checkId(mapOfMoney, unfaithPassID);

        double balance = mapOfMoney.get(unfaithPassID);


        if(amountToPay > balance){
            throw new IllegalArgumentException(
                            "the amount " + amountToPay
                            + " can't be payed with the unfaith pass id " + unfaithPassID
                            + " the balance is too small (" + balance + ")"
            );
        }

        mapOfMoney.put(unfaithPassID, balance - amountToPay);
    }

    public double getBalanceOfPass(int unfaithPassID){
        checkId(mapOfMoney, unfaithPassID);

        return mapOfMoney.get(unfaithPassID);
    }


    public int getPointsOfPass(int unfaithPassID){
        checkId(mapOfPoints, unfaithPassID);

        return mapOfPoints.get(unfaithPassID);
    }

    public void givePointsToPass(int unfaithPassID, int points){

        checkId(mapOfPoints, unfaithPassID);

        int pointsBalance = mapOfPoints.get(unfaithPassID);

        mapOfPoints.put(unfaithPassID,pointsBalance + points);
    }

    /**
     * try to use points to pay
     * @param unfaithPassID
     * @param points
     */
    public void usePoints(int unfaithPassID, int points){

        checkId(mapOfMoney, unfaithPassID);

        int pointsBalance = mapOfPoints.get(unfaithPassID);


        if(points > pointsBalance){
            throw new IllegalArgumentException(
                        "the user with the id " + unfaithPassID
                        + " can't use " + points + " point. It has only "
                        + pointsBalance + " points."
            );
        }

        mapOfPoints.put(unfaithPassID, pointsBalance - points);
    }



    public int createPassAccount(){
        int nextId = 0;

        while(nextId == 0 || mapOfPoints.containsKey(nextId)){
            nextId = (int) (Integer.MAX_VALUE * Math.random());
        }
        mapOfPoints.put(nextId, 0);
        mapOfMoney.put(nextId, 0.0);

        return nextId;
    }

    /* ======= PRIVATE METHODS ======= */

    private void checkId(Map map, int id){
        if(!map.containsKey(id))
            throw new InvalidIdException(id, "unfaith pass");
    }

}

