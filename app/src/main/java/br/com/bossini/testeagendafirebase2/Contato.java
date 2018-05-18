package br.com.bossini.testeagendafirebase2;

import com.google.firebase.database.Exclude;

/**
 * Created by rodrigo on 18/05/18.
 */

public class Contato{
    private String id;
    private String nome, fone, email;

    //na classe Contato
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Contato (String id, String nome, String fone, String email){
        this (nome, fone, email);
        setId(id);
    }
    public Contato (String nome, String fone, String email){
        setNome(nome);
        setFone(fone);
        setEmail(email);
    }
    public Contato (){

    }

    @Override
    public String toString() {
        return "Contato{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", fone='" + fone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
