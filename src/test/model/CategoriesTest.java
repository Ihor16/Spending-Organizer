package model;

import model.exceptions.NameException;
import model.exceptions.NonExistentCategoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriesTest {

    private final String TRAVEL_CATEGORY = "Travel";
    private final String GROCERIES_CATEGORY = "Groceries";

    @BeforeEach
    void setUp() {
        removeAllCategories();
        addCategories();
    }

    @Test
    void testConstructor() {
        removeAllCategories();
        assertTrue(Categories.isEmpty());
    }

    @Test
    void testAddCategory() {
        removeAllCategories();
        assert (Categories.length() == 0);
        try {
            Categories.addCategory(TRAVEL_CATEGORY);
            Categories.addCategory("  " + GROCERIES_CATEGORY + "     ");
        } catch (NameException e) {
            fail("Name is actually acceptable");
            e.printStackTrace();
        }
        assertTrue(Categories.contains(GROCERIES_CATEGORY));
        assertEquals(2, Categories.length());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testAddCategoryThrowNameException(String category) {
        int oldLength = Categories.length();
        assertThrows(NameException.class, () -> Categories.addCategory(category));
        assertEquals(oldLength, Categories.length());
    }

    @Test
    void testRemoveCategory() {
        int oldLength = Categories.length();
        try {
            Categories.removeCategory(GROCERIES_CATEGORY);
        } catch (NonExistentCategoryException e) {
            fail("'" + GROCERIES_CATEGORY + "' category actually exists in the set");
            e.printStackTrace();
        }
        assertEquals(--oldLength, Categories.length());
    }

    @ParameterizedTest
    @ValueSource(strings = {"My Category"})
    void testRemoveCategoryThrowNonExistentCategoryException(String category) {
        int oldLength = Categories.length();
        assertThrows(NonExistentCategoryException.class, () -> Categories.removeCategory(category));
        assertEquals(oldLength, Categories.length());
    }

    @Test
    void testContains() {
        assertTrue(Categories.contains(TRAVEL_CATEGORY));
        assertTrue(Categories.contains(GROCERIES_CATEGORY));
        assertFalse(Categories.contains("My category"));
    }

    @Test
    void testGetCategories() {
        assertEquals(new HashSet<>(Arrays.asList(TRAVEL_CATEGORY, GROCERIES_CATEGORY)), Categories.getCategories());
    }

    @Test
    void testGetCategoriesAsString() {
        assertEquals(TRAVEL_CATEGORY + ", " + GROCERIES_CATEGORY,
                Categories.getCategoriesAsString());
    }

    @Test
    void testGetCategoriesAsStringEmptyCategorySet() {
        removeAllCategories();
        assertEquals("", Categories.getCategoriesAsString());
    }

    // EFFECTS: populates Categories set with categories
    private void addCategories() {
        try {
            Categories.addCategory(TRAVEL_CATEGORY);
            Categories.addCategory(GROCERIES_CATEGORY);
        } catch (NameException e) {
            fail("Name is actually acceptable");
            e.printStackTrace();
        }
    }

    // EFFECTS: removes all categories from the Categories set
    private void removeAllCategories() {
        Categories.getCategories().clear();
    }
}