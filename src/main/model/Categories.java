package model;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import model.exceptions.NameException;
import org.json.JSONArray;
import persistence.WritableArray;

import java.util.StringJoiner;

// Represents set of categories available for user
public class Categories implements WritableArray {

    private Category undefinedCategory;
    private ObservableSet<Category> categories;

    public Categories() {
        this.categories = FXCollections.observableSet();
        try {
            undefinedCategory = new Category("undefined", this);
        } catch (NameException e) {
            assert false;
        }
    }

    // MODIFIES: this
    // EFFECTS: adds category to the set of categories
    public void add(@NotNull Category category) {
        categories.add(category);
    }

    // MODIFIES: this
    // EFFECTS: gives all records with provided category an undefinedCategory,
    //          removes category from categories and returns true if category was removed,
    //          returns false otherwise
    public boolean remove(@NotNull Category category, @NotNull SpendingList spendingList) {
        spendingList.getRecords()
                .filtered(r -> r.getCategory().equals(category))
                .forEach(r -> r.setCategory(undefinedCategory));
        return categories.remove(category);
    }

    public ObservableSet<Category> getCategories() {
        return categories;
    }

    public void setCategories(ObservableSet<Category> categories) {
        this.categories = categories;
    }

    @Override
    // EFFECTS: returns this as JSON Array
    public JSONArray toJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (Category c : categories) {
            jsonArray.put(c.toJsonObject());
        }
        return jsonArray;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Categories.class.getSimpleName() + "[", "]")
                .add("categories=" + categories)
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

        Categories that = (Categories) o;

        return categories.equals(that.categories);
    }

    @Override
    public int hashCode() {
        return categories.hashCode();
    }
}
