package com.fyp.cricintell.models;

public class MatchPrediction {
    private float result;
    private float ground;
    private float wicketsMargin;
    private float runsMargin;
    private float recentYearsFactor;

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }

    public float getGround() {
        return ground;
    }

    public void setGround(float ground) {
        this.ground = ground;
    }

    public float getWicketsMargin() {
        return wicketsMargin;
    }

    public void setWicketsMargin(float wicketsMargin) {
        this.wicketsMargin = wicketsMargin;
    }

    public float getRunsMargin() {
        return runsMargin;
    }

    public void setRunsMargin(float runsMargin) {
        this.runsMargin = runsMargin;
    }

    public float getRecentYearsFactor() {
        return recentYearsFactor;
    }

    public void setRecentYearsFactor(float recentYearsFactor) {
        this.recentYearsFactor = recentYearsFactor;
    }
}
