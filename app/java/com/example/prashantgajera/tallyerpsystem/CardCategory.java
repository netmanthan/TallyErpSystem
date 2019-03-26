package com.example.prashantgajera.tallyerpsystem;

/**
 * Created by Prashant Gajera on 30-Jan-19.
 */

public class CardCategory {

    public int img;
    public String Cardcatagory;

    public CardCategory(int img, String cardcatagory) {
        this.img = img;
        Cardcatagory = cardcatagory;
    }

    public int getImg() {
        return img;
    }

    public String getCardcatagory() {
        return Cardcatagory;
    }
}
