package com.example.projetointegrador.db;

public class User {
    private String idUser;
    private String name;
    private String email;

    public User(){

    }

    public User(String idUser, String name, String email) {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
