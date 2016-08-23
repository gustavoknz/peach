package com.gustavok.peach;

public class Senator {
    private final int id;
    private final String nome;
    private final String partido;
    private final String estado;
    private int voto;
    private int voto2;
    private final String url;

    public Senator(int id, String nome, String partido, String estado, int voto, int voto2, String url) {
        this.id = id;
        this.nome = nome;
        this.partido = partido;
        this.estado = estado;
        this.voto = voto;
        this.voto2 = voto2;
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

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public int getVoto2() {
        return voto2;
    }

    public void setVoto2(int voto2) {
        this.voto2 = voto2;
    }

    public String getUrl() {
        return url;
    }
}
