package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.json.JSONObject;
import persistence.Writable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

// Represents a financial record where user stores their spending
public class Record implements Writable {
    private String title;
    private double amount;
    private String category;
    private LocalDateTime timeAdded;

    // EFFECTS: creates a new record with incremented id, and timeAdded set to now
    public Record() {
        this.timeAdded = LocalDateTime.now();
    }

    // EFFECTS: creates a new record with amount, trimmed title, and trimmed category,
    //          timeAdded set to now,
    //          throws NameException if title or category is blank
    //          throws NegativeAmountException if amount <= 0
    public Record(String title, double amount, String category) throws NameException,
            NegativeAmountException {
        if (isBlank(title)) {
            throw new NameException("title");
        }
        if (amount <= 0) {
            throw new NegativeAmountException();
        }
        if (isBlank(category)) {
            throw new NameException("category");
        }

        this.title = title.trim();
        this.amount = amount;
        this.category = category;
        this.timeAdded = LocalDateTime.now();
    }

    // MODIFIES: this
    // EFFECTS: trims title and assigns it to the record,
    //          throws NameException if provided title is blank
    public void setTitle(String title) throws NameException {
        if (isBlank(title)) {
            throw new NameException("title");
        }
        this.title = title.trim();
    }

    // MODIFIES: this
    // EFFECTS: trims category and assigns it to the record
    //          throws NameException if provided category is blank
    public void setCategory(String category) throws NameException {
        if (isBlank(category)) {
            throw new NameException("category");
        }
        this.category = category.trim();
    }

    // MODIFIES: this
    // EFFECTS: sets record amount,
    //          throws NegativeAmountException if amount is <= 0
    public void setAmount(double amount) throws NegativeAmountException {
        if (amount <= 0) {
            throw new NegativeAmountException();
        }
        this.amount = amount;
    }

    // REQUIREMENT: is used only when reading record from a file
    // MODIFIES: this
    // EFFECTS: sets the time when this was created
    public void setTimeAdded(String timeStamp) {
        this.timeAdded = LocalDateTime.parse(timeStamp);
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

    // EFFECTS: returns true if provided string is blank,
    //          false otherwise
    private boolean isBlank(String str) {
        // implementation of removing whitespaces is taken from
        // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        return str.replaceAll("[\\s]+", "").isEmpty();
    }

    @Override
    // EFFECTS: returns a string representation of record
    public String toString() {
        return new StringJoiner(", ", Record.class.getSimpleName() + "[", "]")
                .add("title='" + title + "'")
                .add("amount=" + amount)
                .add("category='" + category + "'")
                .toString();
    }

    @Override
    // Implementation is based on the Thingy class from JsonSerializationDemo
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("amount", amount);
        jsonObject.put("category", category);
        jsonObject.put("timeAdded", timeAdded);
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Record record = (Record) o;
        if (Double.compare(record.amount, amount) != 0) {
            return false;
        }
        if (!Objects.equals(title, record.title)) {
            return false;
        }
        if (!Objects.equals(category, record.category)) {
            return false;
        }
        return Objects.equals(timeAdded, record.timeAdded);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = title != null ? title.hashCode() : 0;
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (timeAdded != null ? timeAdded.hashCode() : 0);
        return result;
    }
}