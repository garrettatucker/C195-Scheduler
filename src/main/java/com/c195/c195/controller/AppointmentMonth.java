package com.c195.c195.controller;

public class AppointmentMonth {
    private final String month;
    private final int total;

    public AppointmentMonth(String month, int total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public int getTotal() {
        return total;
    }
}
