package model;

import model.exceptions.NotFoundCategoryException;
import model.exceptions.NameException;

import java.util.HashSet;
import java.util.Set;

// Represents list of Categories, user can use only these Categories while working with entries
public class Categories {

    private static Set<String> categories = new HashSet<>();

    // MODIFIES: this
    // EFFECTS: adds a new category to the categories set,
    //          throws WrongCategoryNameException if provided category is empty String
    //          throws WrongCategoryNameException if provided category is empty String
    public static void addCategory(String category) throws NameException {
        if (category.isEmpty()) {
            throw new NameException("category");
        }
        categories.add(category);
    }

    // MODIFIES: this
    // EFFECTS: removes category from the categories set
    //          throws NotFoundCategoryException if category isn't present in the categories set
    public static void removeCategory(String category) throws NotFoundCategoryException {
        if (!contains(category)) {
            throw new NotFoundCategoryException();
        }
        categories.remove(category);
    }

    // EFFECTS: returns the length of categories set
    public static int length() {
        return categories.size();
    }

    // EFFECTS: returns true if the categories set is empty,
    //          false otherwise
    public static boolean isEmpty() {
        return categories.isEmpty();
    }

    // EFFECTS: returns true if entered category is found in the set of categories,
    //          false otherwise
    public static boolean contains(String category) {
        return categories.contains(category);
    }

    public static Set<String> getCategories() {
        return categories;
    }
}
