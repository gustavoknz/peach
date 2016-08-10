package com.gustavok.peach;

public class Senator {
    private final int id;
    private final String nome;
    private final String partido;
    private final String estado;
    private int voto;
    private final String url;

    public Senator(int id, String nome, String partido, String estado, int voto, String url) {
        this.id = id;
        this.nome = nome;
        this.partido = partido;
        this.estado = estado;
        this.voto = voto;
        this.url = url;
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

    public int getVoto() {
        return voto;
    }

    public String getUrl() {
        return url;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }
}
