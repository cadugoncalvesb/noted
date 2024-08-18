package com.example.projetointegrador.db;

public class Item {
    public String idItem;
    public String idList;
    public String nameItem;
    public boolean checked;

    public Item(){

    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    public boolean checked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
    }

    public Item(String nameItem, boolean checked, String idList) {
        this.nameItem = nameItem;
        this.checked = checked;
        this.idList = idList;
    }
}
