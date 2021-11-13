package model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import model.exceptions.NameException;
import org.json.JSONObject;
import persistence.WritableObject;

import java.util.StringJoiner;

// Represents category available for user
public class Category implements WritableObject {

    private final SimpleStringProperty name;
    private final SimpleBooleanProperty isShown;
    private final boolean isDefault;

    public Category(String name, Categories categories) throws NameException {
        if (isBlank(name)) {
            throw new NameException("category");
        }
        this.name = new SimpleStringProperty(name.trim());
        this.isShown = new SimpleBooleanProperty(true);
        this.isDefault = false;
        categories.add(this);
    }

    // REQUIRED: used only when reading from Json file
    public Category(String name, Categories categories, boolean isShown, boolean isDefault)
            throws NameException {
        if (isBlank(name)) {
            throw new NameException("category");
        }
        this.name = new SimpleStringProperty(name.trim());
        this.isShown = new SimpleBooleanProperty(isShown);
        this.isDefault = isDefault;
        categories.add(this);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    // MODIFIES: this
    // EFFECTS: sets name of category,
    //          throws NameException if provided name is invalid
    public void setName(String name) throws NameException {
        if (isBlank(name)) {
            throw new NameException("category");
        }
        this.name.set(name.trim());
    }

    public boolean isShown() {
        return isShown.get();
    }

    public SimpleBooleanProperty isShownProperty() {
        return isShown;
    }

    public void setIsShown(boolean isShown) {
        this.isShown.set(isShown);
    }

    public boolean isDefault() {
        return isDefault;
    }

    // EFFECTS: returns true if provided string is blank,
    //          false otherwise
    private boolean isBlank(String str) {
        // implementation of removing whitespaces is taken from
        // https://stackoverflow.com/a/5455809
        return str.replaceAll("[\\s]+", "").isEmpty();
    }

    @Override
    // EFFECTS: returns this as Json Object
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name.get());
        jsonObject.put("isShown", isShown.get());
        jsonObject.put("isDefault", isDefault);
        return jsonObject;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Category.class.getSimpleName() + "[", "]")
                .add("name=" + name.get())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Category category = (Category) o;

        return name.get().equals(category.name.get());
    }

    @Override
    public int hashCode() {
        return name.get().hashCode();
    }
}
