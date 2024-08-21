package com.example.projetointegrador.db;

import java.util.Date;

public class Lista {
    public String idList;
    public String idUser;
    public String admin;
    public String nameList;
    public String dateModification;

    public Lista(){

    }

    public Lista(String idList, String idUser, String admin, String nameList, String dateModification){
        this.idList = idList;
        this.idUser = idUser;
        this.admin = admin;
        this.nameList = nameList;
        this.dateModification = dateModification;
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

    public String getDateModification() {
        return dateModification;
    }

    public void setDateModification(String dateModification) {
        this.dateModification = dateModification;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

}
