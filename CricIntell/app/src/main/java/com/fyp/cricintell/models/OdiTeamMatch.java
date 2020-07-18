package com.fyp.cricintell.models;

public class OdiTeamMatch {
    private String id;
    private String team;
    private String ground;
    private String location;
    private String margin;
    private String match;
    private String matchDate;
    public OdiTeamMatch(String id, String team, String ground, String location, String margin, String match, String matchDate, String result) {
        this.id = id;
        this.team = team;
        this.ground = ground;
        this.location = location;
        this.margin = margin;
        this.match = match;
        this.matchDate = matchDate;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
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

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private String result;


}
