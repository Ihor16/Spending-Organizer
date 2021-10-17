package model;

import model.exceptions.NegativeAmountException;
import model.exceptions.NotFoundCategoryException;
import model.exceptions.NameException;

import java.time.LocalDateTime;
import java.util.StringJoiner;

// Represents an entity user uses to store their spending
public class Entry {
    private static int nextEntryID = 1;
    private int id;
    private String title;
    private double amount;
    private String category;
    private LocalDateTime timeAdded;

    public Entry() {
        this.id = nextEntryID++;
        this.timeAdded = LocalDateTime.now();
    }

    // REQUIRES: the provided category is available in the Categories List
    // EFFECTS: creates a new entry with parameters specified,
    //          a random id, and timeAdded set to now,
    //          throws WrongTitleException if title is empty
    //          throws NegativeAmountException if amount > 0
    //          throws NotFoundCategoryException if category isn't found in categories set
    //          throws WrongCategoryNameException if given category is empty
    public Entry(String title, double amount, String category) throws NameException,
            NegativeAmountException, NotFoundCategoryException {
        if (title.replaceAll("\\s+", "").isEmpty()) {
            throw new NameException("title");
        }
        if (amount <= 0) {
            throw new NegativeAmountException();
        }
        if (!Categories.contains(category)) {
            throw new NotFoundCategoryException();
        }
        if (category.replaceAll("\\s+", "").isEmpty()) {
            throw new NameException("category");
        }
        // This idea of static nextId is taken from TellerApp
        this.id = nextEntryID++;
        this.title = title;
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

    // EFFECTS: sets the title of the entry,
    //          throws WrongTitleException, if provided title is empty
    public void setTitle(String title) throws NameException {
        if (title.replaceAll("\\s+", "").isEmpty()) {
            throw new NameException("title");
        }
        this.title = title;
    }

    // EFFECTS: sets entry amount,
    //          throws NegativeAmountException, if amount is <= 0
    public void setAmount(double amount) throws NegativeAmountException {
        if (amount <= 0) {
            throw new NegativeAmountException();
        }
        this.amount = amount;
    }

    // EFFECTS: sets the category of entry,
    //          throws NotFoundCategoryException if category isn't found in categories set
    //          throws WrongCategoryNameException if given category is empty
    public void setCategory(String category) throws NotFoundCategoryException, NameException {
        if (category.isEmpty()) {
            throw new NameException("category");
        }
        if (!Categories.contains(category)) {
            throw new NotFoundCategoryException();
        }
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