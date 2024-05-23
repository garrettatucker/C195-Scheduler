package com.c195.c195.controller;

public class AppointmentType {
    private final String type;
    private final int total;

    public AppointmentType(String type, int total) {
        this.type = type;
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public int getTotal() {
        return total;
    }
}
