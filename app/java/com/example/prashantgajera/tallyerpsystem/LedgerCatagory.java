package com.example.prashantgajera.tallyerpsystem;

/**
 * Created by Prashant Gajera on 03-Feb-19.
 */

public class LedgerCatagory {

    public String Name;
    public String OpeningBalance;
    public String ClosingBalance;
    public int img;
    public String group;

    public LedgerCatagory(String name, String openingBalance, String closingBalance,String group, int img) {
        Name = name;
        OpeningBalance = openingBalance;
        ClosingBalance = closingBalance;
        this.img = img;
        this.group=group;
    }

    public String getName() {
        return Name;
    }

    public String getOpeningBalance() {
        return OpeningBalance;
    }

    public String getClosingBalance() {
        return ClosingBalance;
    }

    public int getImg() {
        return img;
    }

    public String getGroup() {
        return group;
    }
}
