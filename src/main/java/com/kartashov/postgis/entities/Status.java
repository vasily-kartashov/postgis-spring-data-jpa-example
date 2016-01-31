package com.kartashov.postgis.entities;

public class Status {

    private double stateOfCharge;

    private String lifeCycle;

    public double getStateOfCharge() {
        return stateOfCharge;
    }

    public void setStateOfCharge(double stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    public String getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(String lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return "{ stateOfCharge: " + stateOfCharge + ", lifeCycle: " + lifeCycle + " }";
    }
}
