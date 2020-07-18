package com.fyp.cricintell.models;

public class PlayerInnings {
    private String id;

    private String name;
    private String country;
    private String opponent;
    private String ground;
    private String inningDate;
    private Float score;
    private Float economyRate;
    private Float strikeRate;

    public PlayerInnings(String id, String name, String country, String opponent, String ground, String inningDate, Float score, Float economyRate,Float strikeRate) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.opponent = opponent;
        this.ground = ground;
        this.inningDate = inningDate;
        this.score = score;
        this.economyRate = economyRate;
        this.strikeRate = strikeRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Float getEconomyRate() {
        return economyRate;
    }

    public void setEconomyRate(Float economyRate) {
        this.economyRate = economyRate;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGround() {
        return ground;
    }

    public void setGround(String ground) {
        this.ground = ground;
    }

    public String getInningDate() {
        return inningDate;
    }

    public void setInningDate(String inningDate) {
        this.inningDate = inningDate;
    }

    public Float getStrikeRate() {
        return strikeRate;
    }

    public void setStrikeRate(Float strikeRate) {
        this.strikeRate = strikeRate;
    }
}
