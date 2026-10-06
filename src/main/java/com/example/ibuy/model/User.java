package com.example.ibuy.model;

import jakarta.persistence.*;

@Entity()
@Table(name = "users")
public class User {

    @Column(unique = true, nullable = false)
    private String mail;
    
    @Id
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String pIva;

    @Column
    private String city;

    @Column
    private String street;

    @Column
    private String cap;

    @Column
    private int num;

    @Column
    private String sessid;

    // getter & setter
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessid() { return sessid; }

    public void setSessid (String sessid) { this.sessid = sessid; }

}
