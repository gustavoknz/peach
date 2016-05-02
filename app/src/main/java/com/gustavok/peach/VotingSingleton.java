package com.gustavok.peach;

public class VotingSingleton {
    public static final int TOTAL_VOTES = 81;//dias
    public static final int REMOVED = 180;//dias

    private static VotingSingleton ourInstance = new VotingSingleton();
    private static boolean votingGoingOn;

    public static VotingSingleton getInstance() {
        return ourInstance;
    }

    private VotingSingleton() {
    }

    public static boolean isVotingGoingOn() {
        return votingGoingOn;
    }

    public static void setVotingGoingOn(boolean votingGoingOn) {
        VotingSingleton.votingGoingOn = votingGoingOn;
    }
}
