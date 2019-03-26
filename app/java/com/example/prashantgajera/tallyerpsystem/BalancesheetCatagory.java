package com.example.prashantgajera.tallyerpsystem;

/**
 * Created by Prashant Gajera on 09-Mar-19.
 */

public class BalancesheetCatagory {

    String balancename;
    String balancevalue;
     public BalancesheetCatagory(String balancename, String balancevalue)
    {
        this.balancename=balancename;
        this.balancevalue=balancevalue;
    }

    public String getBalancename() {
        return balancename;
    }

    public String getBalancevalue() {
        return balancevalue;
    }


}
