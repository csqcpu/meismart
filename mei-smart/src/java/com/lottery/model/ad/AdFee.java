package com.lottery.model.ad;

import java.io.Serializable;

public class AdFee implements Serializable {
    private Integer fee_id;

    private String name;

    private Integer perofnum;

    private Integer price;

    public Integer getFee_id() {
        return fee_id;
    }

    public void setFee_id(Integer fee_id) {
        this.fee_id = fee_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getPerofnum() {
        return perofnum;
    }

    public void setPerofnum(Integer perofnum) {
        this.perofnum = perofnum;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}