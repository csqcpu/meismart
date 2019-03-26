package com.lottery.model.ad;

import java.io.Serializable;

public class AdPay implements Serializable{
    private Integer pay_id;

    private String name;

    public Integer getPay_id() {
        return pay_id;
    }

    public void setPay_id(Integer pay_id) {
        this.pay_id = pay_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}