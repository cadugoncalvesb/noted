package com.example.projetointegrador;

import java.util.Date;

public class Lista {
    public String idList;
    public String nameList;
    public Date dateModification;
    public String criador;

    public Lista(){

    }

    public Lista(String nameList, Date dateModification, String idList, String criador){
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

    public String getCriador() {
        return criador;
    }

    public void setCriador(String criador) {
        this.criador = criador;
    }

    public Item get(int position) {
        return null;
    }
}
