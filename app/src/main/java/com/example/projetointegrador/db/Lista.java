package com.example.projetointegrador.db;

import com.google.firebase.Timestamp;

public class Lista {
    public String idList;
    public String admin;
    public String idUser;
    public String nameList;
    public Timestamp dateCreate;
    public String dateModification;

    public Lista(){

    }

    public Lista(String admin, String nameList, Timestamp dateCreate, String dateModification){
        this.admin = admin;
        this.nameList = nameList;
        this.dateCreate = dateCreate;
        this.dateModification = dateModification;
    }

    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameList() {
        return nameList;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }

    public Timestamp getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Timestamp dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getDateModification() {
        return dateModification;
    }

    public void setDateModification(String dateModification) {
        this.dateModification = dateModification;
    }

}
