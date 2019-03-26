package com.lottery.model.ad;

import java.io.Serializable;

public class AdFee implements Serializable{
    private Integer fee_id;

    private String name;

    private Double price;

    private String method;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }
}