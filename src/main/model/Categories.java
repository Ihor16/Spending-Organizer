package model;

import model.exceptions.NameException;
import model.exceptions.NonExistentCategoryException;
import org.json.JSONArray;
import persistence.WritableArray;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// Represents list of Categories, user can use only these Categories while working with entries
public class Categories implements WritableArray {

    private final Set<String> categories;

    public Categories() {
        categories = new HashSet<>();
    }

    // MODIFIES: this
    // EFFECTS: trims the given category and adds it to the categories set,
    //          throws NameException if provided category is a blank String
    public void addCategory(String category) throws NameException {
        if (isBlank(category)) {
            throw new NameException("category");
        }
        categories.add(category.trim());
    }

    // MODIFIES: this
    // EFFECTS: removes category from the categories set and returns true,
    //          throws NonExistentCategoryException if category isn't present in the categories set
    public void removeCategory(String category) throws NonExistentCategoryException {
        if (!categories.remove(category)) {
            throw new NonExistentCategoryException(category);
        }
    }

    public Set<String> getCategories() {
        return categories;
    }

    // EFFECTS: returns the size of categories set
    public int size() {
        return categories.size();
    }

    // EFFECTS: returns true if the categories set is empty,
    //          false otherwise
    public boolean isEmpty() {
        return categories.isEmpty();
    }

    // EFFECTS: returns true if entered category is found in the set of categories,
    //          false otherwise
    public boolean contains(String category) {
        return categories.contains(category);
    }


    @Override
    public JSONArray toJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (String category : categories) {
            jsonArray.put(category);
        }
        return jsonArray;
    }

    // EFFECTS: returns true if the given string is blank
    private boolean isBlank(String category) {
        // implementation of removing whitespaces is taken from
        // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        return category.replaceAll("[\\s]+", "").isEmpty();
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
        return Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return categories.hashCode();
    }
}