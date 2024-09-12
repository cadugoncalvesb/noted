package com.example.projetointegrador.db;

public class Item {
    public String idItem;
    public String idList;
    public String nameItem;
    public boolean checked;
    public String unidade;
    public int quantidade;
    public float preco;

    public Item(){

    }

    public Item(String idList, String nameItem, boolean checked,
                String unidade, int quantidade, float preco) {
        this.idList = idList;
        this.nameItem = nameItem;
        this.checked = checked;
        this.unidade = unidade;
        this.quantidade = quantidade;
        this.preco = preco;
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

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }
}
