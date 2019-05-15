package com.lottery.model.ad;

import java.io.Serializable;
import java.util.Date;

public class AdPublish implements Serializable {
    private Integer publish_id;

    private String title;

    private Integer location_id;

    private Date startdt;

    private Date enddt;

    private Integer fee_id;

    private Integer price;

    private String createuser;

    private Date createdt;

    private String checkuser;

    private Date checkdt;

    private Byte status;

    public Integer getPublish_id() {
        return publish_id;
    }

    public void setPublish_id(Integer publish_id) {
        this.publish_id = publish_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Integer getLocation_id() {
        return location_id;
    }

    public void setLocation_id(Integer location_id) {
        this.location_id = location_id;
    }

    public Date getStartdt() {
        return startdt;
    }

    public void setStartdt(Date startdt) {
        this.startdt = startdt;
    }

    public Date getEnddt() {
        return enddt;
    }

    public void setEnddt(Date enddt) {
        this.enddt = enddt;
    }

    public Integer getFee_id() {
        return fee_id;
    }

    public void setFee_id(Integer fee_id) {
        this.fee_id = fee_id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser == null ? null : createuser.trim();
    }

    public Date getCreatedt() {
        return createdt;
    }

    public void setCreatedt(Date createdt) {
        this.createdt = createdt;
    }

    public String getCheckuser() {
        return checkuser;
    }

    public void setCheckuser(String checkuser) {
        this.checkuser = checkuser == null ? null : checkuser.trim();
    }

    public Date getCheckdt() {
        return checkdt;
    }

    public void setCheckdt(Date checkdt) {
        this.checkdt = checkdt;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}