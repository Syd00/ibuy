package com.example.ibuy.model;

import jakarta.persistence.*;

@Entity(name = "utente")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUtente")
    private long idUtente;

    @Column(unique = true, nullable = false)
    private String mail;

    @Column(unique = true, nullable = false)
    private String username;

    // getter & setter
    public Long getIdUtente() { return idUtente; }
    public void setIdUtente(Long idUtente) { this.idUtente = idUtente; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
