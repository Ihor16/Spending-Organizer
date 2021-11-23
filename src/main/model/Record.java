package model;

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

    private final SimpleStringProperty title;
    private final SimpleDoubleProperty amount;
    private final SimpleObjectProperty<Category> category;
    private final SimpleObjectProperty<LocalDateTime> timeAdded;
    private final EventLog log = EventLog.getInstance();

    // REQUIRED: used only while reading from Json file and testing
    // EFFECTS: creates a new record with timeAdded set to now
    public Record() {
        this.timeAdded = new SimpleObjectProperty<>(LocalDateTime.now());
        this.title = new SimpleStringProperty("");
        this.amount = new SimpleDoubleProperty(0);
        this.category = new SimpleObjectProperty<>(null);
    }

    // EFFECTS: creates a new record with trimmed title, amount, category,
    //          timeAdded set to now,
    //          throws NameException if title is blank
    //          throws NegativeAmountException if amount < 0
    public Record(String title, double amount, Category category) throws NameException,
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
    public void setTitle(String title) throws NameException {
        if (isBlank(title)) {
            log.logEvent(new Event("NameException thrown because new Record's name is blank"));
            throw new NameException("title");
        }
        this.title.set(title.trim());
        log.logEvent(new Event("Record's title set to: " + getTitle()));
    }

    // MODIFIES: this
    // EFFECTS: sets record amount,
    //          throws NegativeAmountException if amount is < 0
    public void setAmount(double amount) throws NegativeAmountException {
        if (amount < 0) {
            log.logEvent(new Event("NegativeAmountException thrown because new Record's amount is < 0"));
            throw new NegativeAmountException();
        }
        this.amount.set(amount);
        log.logEvent(new Event("Record's amount set to: " + getAmount()));
    }

    public void setCategory(Category category) {
        this.category.set(category);
        log.logEvent(new Event("Record's category set to: " + getCategory()));
    }

    // INVARIANT: is used only when reading record from a file
    public void setTimeAdded(String timeStamp) {
        this.timeAdded.set(LocalDateTime.parse(timeStamp));
        log.logEvent(new Event("Record's timeAdded set to: " + getTimeAdded()));
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
    private boolean isBlank(String str) {
        // implementation of removing whitespaces is taken from
        // https://stackoverflow.com/a/5455809
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