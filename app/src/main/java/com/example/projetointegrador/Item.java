package com.example.projetointegrador;

public class Item {
    public String idItem;
    public String idList;
    public String nameItem;
    public boolean isChecked;

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
    }

    public Item(String idItem, String nameItem, boolean isChecked, String idList) {
        this.idItem = idItem;
        this.nameItem = nameItem;
        this.isChecked = isChecked;
        this.idList = idList;
    }
}
