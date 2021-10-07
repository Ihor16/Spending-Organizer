package model;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class Entry {
    private static int nextEntryID = 1;
    private int id;
    private String title;
    private double amount;
    private String category;
    private LocalDateTime timeAdded;

    // REQUIRES: the provided category is available in the Categories List
    // EFFECTS: creates a new entry with parameters specified,
    //          a random id, and timeAdded set to now
    public Entry(String title, double amount, String category) {
        // This idea of static nextId is taken from TellerApp
        this.id = nextEntryID++;
        this.title = title;
        this.amount = amount;
        this.category = category;
        timeAdded = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getTimeAdded() {
        return timeAdded;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // REQUIRES: the provided category is available in the Categories set
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    // EFFECTS: returns a string representation of Entry
    public String toString() {
        return new StringJoiner(", ", Entry.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("title='" + title + "'")
                .add("amount=" + amount)
                .add("category='" + category + "'")
                .toString();
    }
}