package com.example.projetointegrador;

import java.util.Date;

public class List {
    public String idList;
    public String nameList;
    public Date dateModification;

    public List(){

    }

    public List(String nameList, Date dateModification, String idList){
        this.nameList = nameList;
        this.dateModification = dateModification;
        this.idList = idList;
    }
    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
    }

    public String getNameList() {
        return nameList;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }
}
