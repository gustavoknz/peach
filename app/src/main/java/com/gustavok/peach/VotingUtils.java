package com.gustavok.peach;

public final class VotingUtils {
    public static final int TOTAL_VOTES = 81;
    public static final int REMOVED_DAYS = 180; //days

    public static final int DEFAULT_VALUE = 0;
    public static final int YES = 1;
    public static final int NO = 2;
    public static final int ABSENCE = 3;
    public static final int ABSTENTION = 4;
    public static final int UNKNOWN = 5;

    public static final int CONSTRAINT_ALL = 0;
    public static final int CONSTRAINT_NO_VOTE = 1;
    public static final int CONSTRAINT_YES = 2;
    public static final int CONSTRAINT_NO = 3;
    public static final int CONSTRAINT_ABSTENTION = 4;
    public static final int CONSTRAINT_ABSENCE = 5;

    private static boolean votingGoingOn = true;

    public static boolean isVotingGoingOn() {
        return votingGoingOn;
    }

    public static void votingFinished() {
        votingGoingOn = false;
    }
}
