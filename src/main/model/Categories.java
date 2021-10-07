package model;

import java.util.HashSet;
import java.util.Set;

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

    // MODIFIES: this
    // EFFECTS: removes category from the categories set
    public void removeCategory(String category) {
        categories.remove(category);
    }

    public Set<String> getCategories() {
        return categories;
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
}
