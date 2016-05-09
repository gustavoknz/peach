package com.gustavok.peach;

public class Senator {
    private int id;
    private String nome;
    private String partido;
    private String estado;
    private String voto;
    private String url;

    public Senator(int id, String nome, String partido, String estado, String voto, String url) {
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

    public String getVoto() {
        return voto;
    }

    public String getUrl() {
        return url;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }
}
