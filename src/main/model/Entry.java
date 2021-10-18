package model;

import model.exceptions.NegativeAmountException;
import model.exceptions.NonExistentCategoryException;
import model.exceptions.NameException;

import java.time.LocalDateTime;
import java.util.StringJoiner;

// Represents a financial entry where user stores their spending
public class Entry {
    private static int nextEntryID = 1;
    private final int id;
    private String title;
    private double amount;
    private String category;
    private final LocalDateTime timeAdded;

    // EFFECTS: creates a new entry with incremented id, and timeAdded set to now
    public Entry() {
        this.id = nextEntryID++;
        this.timeAdded = LocalDateTime.now();
    }

    // EFFECTS: creates a new entry with parameters specified (trimmed title),
    //          incremented id, and timeAdded set to now,
    //          throws NameException if title is blank
    //          throws NegativeAmountException if amount <= 0
    // INVARIANT: category is always acceptable
    public Entry(String title, double amount, String category) throws NameException,
            NegativeAmountException {
        if (isBlank(title)) {
            throw new NameException("title");
        }
        if (amount <= 0) {
            throw new NegativeAmountException();
        }
        // This idea of static nextId is taken from TellerApp
        this.id = nextEntryID++;
        this.title = title.trim();
        this.amount = amount;
        this.category = category;
        this.timeAdded = LocalDateTime.now();
    }

    public int getId() {
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

    // MODIFIES: this
    // EFFECTS: trims title and sets it to the entry,
    //          throws NameException if provided title is blank
    public void setTitle(String title) throws NameException {
        if (isBlank(title)) {
            throw new NameException("title");
        }
        this.title = title.trim();
    }

    // MODIFIES: this
    // EFFECTS: sets entry amount,
    //          throws NegativeAmountException if amount is <= 0
    public void setAmount(double amount) throws NegativeAmountException {
        if (amount <= 0) {
            throw new NegativeAmountException();
        }
        this.amount = amount;
    }

    // MODIFIES: this
    // EFFECTS: assigns category to the entry
    // INVARIANT: category is always acceptable
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

    // EFFECTS: returns true if provided string is blank,
    //          false otherwise
    private boolean isBlank(String str) {
        // implementation of removing whitespaces is taken from
        // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        return str.replaceAll("[\\s]+", "").isEmpty();
    }
}