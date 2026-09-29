package com.quanlykhachsan.services;

public class Item {
    public String id;
    public String name;
    public String extra;
    public String text;
    public double amount;

    public Item() {}
    public Item(String id, String name, String extra, String text, double amount) {
        this.id=id; this.name=name; this.extra=extra; this.text=text; this.amount=amount;
    }

    @Override public String toString() {
        return name == null || name.isBlank() ? id : name;
    }
}
