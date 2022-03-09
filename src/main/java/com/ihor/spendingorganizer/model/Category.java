package com.ihor.spendingorganizer.model;

import com.ihor.spendingorganizer.model.exceptions.NameException;
import com.ihor.spendingorganizer.persistence.WritableObject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.json.JSONObject;

import java.util.StringJoiner;

// Represents category available for user
public class Category implements WritableObject {

    private final SimpleStringProperty name;
    private final SimpleBooleanProperty isShown;
    private final boolean isDefault;
    private final EventLog log = EventLog.getInstance();

    public Category(String name, Categories categories) throws NameException {
        if (name.isBlank()) {
            throw new NameException("category");
        }
        this.name = new SimpleStringProperty(name.trim());
        this.isShown = new SimpleBooleanProperty(true);
        this.isDefault = false;
        categories.add(this);
    }

    // REQUIRED: used only when reading from Json file or initializing a default category
    public Category(String name, Categories categories, boolean isShown, boolean isDefault)
            throws NameException {
        if (name.isBlank()) {
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
    //          throws NameException if provided name is invalid,
    //          throws NameException if category with provided name already exists
    public void setName(String name, Categories categories) throws NameException {
        String n = name.trim();
        if (n.isBlank()) {
            log.logEvent(new Event("NameException thrown because new Category's name is blank"));
            throw new NameException("category");
        } else if (categories.getCategoriesNames().contains(n)) {
            log.logEvent(new Event("NameException thrown because user tried to add same category"));
            throw new NameException();
        }
        this.name.set(n);
        log.logEvent(new Event("Category's name changed to " + getName()));
    }

    public boolean isShown() {
        return isShown.get();
    }

    public SimpleBooleanProperty isShownProperty() {
        return isShown;
    }

    public void setIsShown(boolean isShown) {
        this.isShown.set(isShown);
        log.logEvent(new Event("Category's " + getName() + " isShown is set to " + isShown()));
    }

    public boolean isDefault() {
        return isDefault;
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
