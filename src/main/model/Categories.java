package model;

import java.util.HashSet;
import java.util.Set;

// Represents list of Categories, user can use only these Categories while working with entries
public class Categories {

    private Set<String> categories;

    // EFFECTS: creates empty categories set
    public Categories() {
        categories = new HashSet<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a new category to the categories set
    public void addCategory(String category) {
        categories.add(category);
    }

    // REQUIRES: category is present in the categories set
    // MODIFIES: this
    // EFFECTS: removes category from the categories set
    public void removeCategory(String category) {
        categories.remove(category);
    }

    // EFFECTS: returns the length of categories set
    public int length() {
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

    public Set<String> getCategories() {
        return categories;
    }

    @Override
    // EFFECTS: returns string of categories separated by commas
    public String toString() {
        return String.join(", ", categories);
    }
}
