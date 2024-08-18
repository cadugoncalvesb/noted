package com.example.projetointegrador.db;

import java.util.Date;

public class Lista {
    public String idList;
    public String idUser;
    public String admin;
    public String nameList;
    public Date dateModification;

    public Lista(){

    }

    public Lista(String idList, Date dateModification, String nameList, String admin){
        this.idList = idList;
        this.nameList = nameList;
        this.dateModification = dateModification;
        this.admin = admin;
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

}
