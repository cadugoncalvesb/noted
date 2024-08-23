package com.example.projetointegrador.db;

public class Item {
    public String idItem;
    public String idList;
    public String nameItem;
    public boolean checked;

    public Item(){

    }

    public Item(String idList, String nameItem, boolean checked) {
        this.idList = idList;
        this.nameItem = nameItem;
        this.checked = checked;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
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
}
