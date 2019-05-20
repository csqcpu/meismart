package com.lottery.model.stb;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

public class StbModel {
    private Integer stb_id;

    private String name;

    private String model;

    private String resolution;

    private String createuser;

    private Date createdt;

    private String checkuser;

    private Date checkdt;

    private Integer status;
    
    private JSONObject perm;

    public Integer getStb_id() {
        return stb_id;
    }

    public void setStb_id(Integer stb_id) {
        this.stb_id = stb_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution == null ? null : resolution.trim();
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public JSONObject getPerm() {
        return perm;
    }

    public void setPerm(JSONObject perm) {
        this.perm = perm;
    }
}