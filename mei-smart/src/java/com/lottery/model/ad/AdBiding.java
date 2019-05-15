package com.lottery.model.ad;

import java.io.Serializable;

public class AdBiding implements Serializable {
    private Integer biding_id;

    private Integer publish_id;

    private Integer price;

    private String username;

    private Integer content_id;

    private Byte status;

    public Integer getBiding_id() {
        return biding_id;
    }

    public void setBiding_id(Integer biding_id) {
        this.biding_id = biding_id;
    }

    public Integer getPublish_id() {
        return publish_id;
    }

    public void setPublish_id(Integer publish_id) {
        this.publish_id = publish_id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Integer getContent_id() {
        return content_id;
    }

    public void setContent_id(Integer content_id) {
        this.content_id = content_id;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}