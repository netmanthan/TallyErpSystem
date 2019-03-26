package com.example.prashantgajera.tallyerpsystem;

/**
 * Created by Prashant Gajera on 26-Feb-19.
 */

public class VoucherProfileCatagory {
    String Name;
    String Date;
    String VoucherNo;

    VoucherProfileCatagory(String name,String date,String VoucherNo)
    {
        Name=name;
        Date=date;
        this.VoucherNo=VoucherNo;
    }

    public String getName() {
        return Name;
    }

    public String getDate() {
        return Date;
    }

    public String getVoucherNo(){ return VoucherNo;}

}
