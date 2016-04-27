package com.gustavok.peach;

import java.util.Calendar;

public class Senator {
    private boolean voteYes;
    private String name;
    private Calendar birthDate;
    private String bornCity;
    private String party;
    private String state;
    private String phone;
    private String email;
    private String site;
    private String imageSrc;

    public Senator(String name, Calendar birthDate, String bornCity, String party, String state,
                   String phone, String email, String site, String imageSrc) {
        this.name = name;
        this.bornCity = bornCity;
        this.birthDate = birthDate;
        this.party = party;
        this.state = state;
        this.phone = phone;
        this.email = email;
        this.site = site;
        this.imageSrc = imageSrc;
    }

    public boolean isVoteYes() {
        return voteYes;
    }

    public String getName() {
        return name;
    }

    public Calendar getBirthDate() {
        return birthDate;
    }

    public String getBornCity() {
        return bornCity;
    }

    public String getParty() {
        return party;
    }

    public String getState() {
        return state;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getSite() {
        return site;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setVoteYes(boolean voteYes) {
        this.voteYes = voteYes;
    }
}
