package model;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.exceptions.NameException;
import org.json.JSONArray;
import persistence.WritableArray;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

// Represents categories user can choose from
// INVARIANT: defaultCategory is always present
public class Categories implements WritableArray {

    private Category defaultCategory;
    private final ObservableList<Category> categories;

    public Categories() {
        this.categories = FXCollections.observableArrayList();
        try {
            defaultCategory = new Category("default", this);
        } catch (NameException e) {
            assert false;
        }
    }

    // MODIFIES: this
    // EFFECTS: adds category to the categories if category isn't already there
    public void add(@NotNull Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    // MODIFIES: this
    // EFFECTS: if category is defaultCategory, does nothing
    //          otherwise, sets all spendingList's records with category to defaultCategory,
    //          and removes category from categories
    public void remove(@NotNull Category category, @NotNull SpendingList spendingList) {
        if (!category.equals(defaultCategory)) {
            spendingList.getRecords()
                    .filtered(r -> r.getCategory().equals(category))
                    .forEach(r -> r.setCategory(defaultCategory));
            categories.remove(category);
        }
    }

    // EFFECTS: returns category with name,
    //          if not found, returns defaultCategory
    public Category getCategoryByName(String name) {
        return categories.filtered(c -> c.getName().equals(name))
                .stream().findAny()
                .orElse(defaultCategory);
    }

    // EFFECTS: returns a list of all names of categories
    public List<String> getCategoriesNames() {
        return categories.stream().map(Category::getName)
                .collect(Collectors.toList());
    }

    // MODIFIES: this
    // EFFECTS: sets categories, and adds a defaultCategory as well
    public void setCategories(@NotNull List<Category> categories) {
        this.categories.clear();
        this.categories.add(defaultCategory);
        this.categories.addAll(categories);
    }

    public ObservableList<Category> getCategories() {
        return categories;
    }

    public Category getDefaultCategory() {
        return defaultCategory;
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
