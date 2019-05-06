package com.lottery.model.auth;

import java.io.Serializable;
import java.util.Date;

public class Perm  implements Serializable {
    private String select;

    private String insert;
    
    private String update;    

    private String delete;
    
    private String commit;    
    
    private String check;
    
    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select == null ? null : select.trim();
    }

    public String getInsert() {
        return insert;
    }

    public void setInsert(String insert) {
        this.insert = insert == null ? null : insert.trim();
    }
    
    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update == null ? null: update.trim();
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete == null ? null: delete.trim();
    }
    
    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit == null ? null: commit.trim();
    }
    
    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check == null ? null: check.trim();
    }    
}