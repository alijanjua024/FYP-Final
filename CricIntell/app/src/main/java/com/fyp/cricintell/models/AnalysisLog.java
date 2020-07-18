package com.fyp.cricintell.models;

public class AnalysisLog  {
    private String team;
    private String teamFlag;
    private String opponent;

    public AnalysisLog(String team, String teamFlag, String opponent, String opponentFlag, String ground, String location, int entryType) {
        this.team = team;
        this.teamFlag = teamFlag;
        this.opponent = opponent;
        this.opponentFlag = opponentFlag;
        this.ground = ground;
        this.location = location;
        this.entryType = entryType;
    }

    private String opponentFlag;
    private String ground;
    private String location;
    private int entryType;


    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamFlag() {
        return teamFlag;
    }

    public void setTeamFlag(String teamFlag) {
        this.teamFlag = teamFlag;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getOpponentFlag() {
        return opponentFlag;
    }

    public void setOpponentFlag(String opponentFlag) {
        this.opponentFlag = opponentFlag;
    }

    public String getGround() {
        return ground;
    }

    public void setGround(String ground) {
        this.ground = ground;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEntryType() {
        return entryType;
    }

    public void setEntryType(int entryType) {
        this.entryType = entryType;
    }



}
