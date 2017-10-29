package com.lab.lauri.amicaapp;

/**
 * Luokan on luonut tuomo päivämäärällä 29.10.2017.
 */

public class MealObject {

    private String name;
    private int id;
    private int sortOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
