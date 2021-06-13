package com.hcr2bot.coronatracker;

public class StateList {
    private String StateName;
    private int ConfirmedCases;

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public int getConfirmedCases() {
        return ConfirmedCases;
    }

    public void setConfirmedCases(int confirmedCases) {
        ConfirmedCases = confirmedCases;
    }
}
