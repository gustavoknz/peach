package com.gustavok.peach;

import android.graphics.Bitmap;

public class Senator {
    private int id;
    private String nome;
    private String partido;
    private String estado;
    private String voto;
    private String url;
    private Bitmap imagem;

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

    public Bitmap getImagem() {
        return imagem;
    }

    public void setImagem(Bitmap imagem) {
        this.imagem = imagem;
    }
}
