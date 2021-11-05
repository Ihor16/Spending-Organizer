package model;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.json.JSONObject;
import persistence.WritableObject;

import java.time.LocalDateTime;
import java.util.StringJoiner;

// Represents a financial record where user stores their spending
public class Record implements WritableObject {
    private SimpleStringProperty title;
    private SimpleDoubleProperty amount;
    private SimpleObjectProperty<Category> category;
    private SimpleObjectProperty<LocalDateTime> timeAdded;

    // REQUIRED: used only while reading from Json file and testing
    // EFFECTS: creates a new record with timeAdded set to now
    public Record() {
        this.timeAdded = new SimpleObjectProperty<>(LocalDateTime.now());
    }

    // EFFECTS: creates a new record with trimmed title, amount, category,
    //          timeAdded set to now,
    //          throws NameException if title is blank
    //          throws NegativeAmountException if amount < 0
    public Record(@NotNull String title, double amount, @NotNull Category category) throws NameException,
            NegativeAmountException {

        if (isBlank(title)) {
            throw new NameException("title");
        }
        if (amount < 0) {
            throw new NegativeAmountException();
        }

        this.title = new SimpleStringProperty(title.trim());
        this.amount = new SimpleDoubleProperty(amount);
        this.category = new SimpleObjectProperty<>(category);
        this.timeAdded = new SimpleObjectProperty<>(LocalDateTime.now());
    }

    // MODIFIES: this
    // EFFECTS: trims title and assigns it to the record,
    //          throws NameException if provided title is blank
    public void setTitle(@NotNull String title) throws NameException {
        if (isBlank(title)) {
            throw new NameException("title");
        }
        this.title = new SimpleStringProperty(title.trim());
    }

    // MODIFIES: this
    // EFFECTS: sets record amount,
    //          throws NegativeAmountException if amount is < 0
    public void setAmount(double amount) throws NegativeAmountException {
        if (amount < 0) {
            throw new NegativeAmountException();
        }
        this.amount = new SimpleDoubleProperty(amount);
    }

    public void setCategory(@NotNull Category category) {
        this.category = new SimpleObjectProperty<>(category);
    }

    // INVARIANT: is used only when reading record from a file
    public void setTimeAdded(@NotNull String timeStamp) {
        this.timeAdded = new SimpleObjectProperty<>(LocalDateTime.parse(timeStamp));
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public double getAmount() {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }

    public Category getCategory() {
        return category.get();
    }

    public SimpleObjectProperty<Category> categoryProperty() {
        return category;
    }

    public LocalDateTime getTimeAdded() {
        return timeAdded.get();
    }

    public SimpleObjectProperty<LocalDateTime> timeAddedProperty() {
        return timeAdded;
    }

    // EFFECTS: returns true if provided string is blank,
    //          false otherwise
    private boolean isBlank(@NotNull String str) {
        // implementation of removing whitespaces is taken from
        // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        return str.replaceAll("[\\s]+", "").isEmpty();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Record.class.getSimpleName() + "[", "]")
                .add("title=" + title.get())
                .add("amount=" + amount.get())
                .add("category=" + category.get())
                .add("timeAdded=" + timeAdded.get())
                .toString();
    }

    @Override
    // EFFECTS: returns this as JSON Object
    // Implementation is based on the Thingy class from JsonSerializationDemo
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title.get());
        jsonObject.put("amount", amount.get());
        jsonObject.put("category", category.get().toJsonObject());
        jsonObject.put("timeAdded", timeAdded.get());
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

        if (!title.get().equals(record.title.get())) {
            return false;
        }
        if (!amount.getValue().equals(record.amount.getValue())) {
            return false;
        }
        if (!category.get().equals(record.category.get())) {
            return false;
        }
        return timeAdded.get().equals(record.timeAdded.get());
    }

    @Override
    public int hashCode() {
        int result = title.get().hashCode();
        result = 31 * result + amount.getValue().hashCode();
        result = 31 * result + category.get().hashCode();
        result = 31 * result + timeAdded.get().hashCode();
        return result;
    }
}