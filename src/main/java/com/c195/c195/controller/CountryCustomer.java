package com.c195.c195.controller;

public class CountryCustomer {
    private final String country;
    private final int customerCount;

    public CountryCustomer(String country, int customerCount) {
        this.country = country;
        this.customerCount = customerCount;
    }

    public String getCountry() {
        return country;
    }

    public int getCustomerCount() {
        return customerCount;
    }
}
