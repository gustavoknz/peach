package com.gustavok.peach;

import java.util.Calendar;

public class Senator {
    private int id;
    private String nome;
    private String partido;
    private String estado;
    private String voto;

    public Senator(int id, String nome, String partido, String estado, String voto) {
        this.id = id;
        this.nome = nome;
        this.partido = partido;
        this.estado = estado;
        this.voto = voto;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPartido() {
        return partido;
    }

    public String getEstado() {
        return estado;
    }

    public String getVoto() {
        return voto;
    }
}
