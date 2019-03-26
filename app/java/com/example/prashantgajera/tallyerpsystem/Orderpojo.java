package com.example.prashantgajera.tallyerpsystem;

import java.util.Calendar;
import java.util.Date;

public class Orderpojo {
    String title;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public Orderpojo(){
        Date currentTime = Calendar.getInstance().getTime();
        time= currentTime.toString();
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrderdata() {
        return orderdata;
    }

    public void setOrderdata(String orderdata) {
        this.orderdata = orderdata;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String orderdata;
    String time;
}
