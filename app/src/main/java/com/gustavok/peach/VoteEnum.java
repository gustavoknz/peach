package com.gustavok.peach;

public enum VoteEnum {

    YES(0), //SIM
    NO(1), //NAO
    ABSTENTION(2), //ABSTENCAO
    ABSENCE(3), //AUSENCIA
    UNKNOWN(4);

    private final int vote;

    VoteEnum(int vote) {
        this.vote = vote;
    }

    public static VoteEnum get(int vote) {
        return get(vote);
    }

    public int getVote() {
        return this.vote;
    }
}
