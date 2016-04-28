package com.gustavok.peach;

public class SenatorVote {
    private int id;
    private int vote;

    public SenatorVote(int id, int vote) {
        this.id = id;
        this.vote = vote;
    }

    public int getId() {
        return id;
    }

    public int getVote() {
        return vote;
    }
}
