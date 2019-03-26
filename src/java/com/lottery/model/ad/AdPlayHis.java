package com.lottery.model.ad;

import java.io.Serializable;
import java.util.Date;

public class AdPlayHis implements Serializable{
    private Integer playhis_id;

    private Integer online_id;

    private Date startdt;

    private Date enddt;

    private Integer playtime;

    private Date insertdt;

    public Integer getPlayhis_id() {
        return playhis_id;
    }

    public void setPlayhis_id(Integer playhis_id) {
        this.playhis_id = playhis_id;
    }

    public Integer getOnline_id() {
        return online_id;
    }

    public void setOnline_id(Integer online_id) {
        this.online_id = online_id;
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

    public Integer getPlaytime() {
        return playtime;
    }

    public void setPlaytime(Integer playtime) {
        this.playtime = playtime;
    }

    public Date getInsertdt() {
        return insertdt;
    }

    public void setInsertdt(Date insertdt) {
        this.insertdt = insertdt;
    }
}