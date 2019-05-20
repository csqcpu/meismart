package com.lottery.model.ad;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

public class AdLocation {
    private Integer location_id;

    private Integer stb_id;

    private String name;

    private Byte type_id;

    private String description;

    private String imageurl;

    private String createuser;

    private Date createdt;

    private String checkuser;

    private Date checkdt;

    private Integer status;
    
    private JSONObject perm;

    public Integer getLocation_id() {
        return location_id;
    }

    public void setLocation_id(Integer location_id) {
        this.location_id = location_id;
    }

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

    public Byte getType_id() {
        return type_id;
    }

    public void setType_id(Byte type_id) {
        this.type_id = type_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl == null ? null : imageurl.trim();
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