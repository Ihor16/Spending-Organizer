package model;

import model.exceptions.NonExistentCategoryException;
import model.exceptions.NameException;

import java.util.HashSet;
import java.util.Set;

// Represents list of Categories, user can use only these Categories while working with entries
public class Categories {

    private static final Set<String> CATEGORIES = new HashSet<>();

    // MODIFIES: this
    // EFFECTS: trims the given category and adds it to the categories set,
    //          throws NameException if provided category is a blank String
    public static void addCategory(String category) throws NameException {
        // implementation of removing whitespaces is taken from
        // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        if (category.replaceAll("[\\s]+", "").isEmpty()) {
            throw new NameException("category");
        }
        CATEGORIES.add(category.trim());
    }

    // MODIFIES: this
    // EFFECTS: removes category from the categories set
    //          throws NonExistentCategoryException if category isn't present in the categories set
    public static void removeCategory(String category) throws NonExistentCategoryException {
        if (!contains(category)) {
            throw new NonExistentCategoryException();
        }
        CATEGORIES.remove(category);
    }

    // EFFECTS: returns the length of categories set
    public static int length() {
        return CATEGORIES.size();
    }

    // EFFECTS: returns true if the categories set is empty,
    //          false otherwise
    public static boolean isEmpty() {
        return CATEGORIES.isEmpty();
    }

    // EFFECTS: returns true if entered category is found in the set of categories,
    //          false otherwise
    public static boolean contains(String category) {
        return CATEGORIES.contains(category);
    }

    public static Set<String> getCategories() {
        return CATEGORIES;
    }

    // EFFECTS: returns a String of categories separated by commas
    public static String getCategoriesAsString() {
        return String.join(", ", CATEGORIES);
    }
}
