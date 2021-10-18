package model;

import model.exceptions.NameException;
import model.exceptions.NonExistentCategoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriesTest {

    private Categories categories;
    private final String TRAVEL_CATEGORY = "Travel in Canada";
    private final String GROCERIES_CATEGORY = "Groceries";

    @BeforeEach
    void setUp() {
        categories = new Categories();
        try {
            initCategories();
        } catch (NameException e) {
            fail("These categories are actually acceptable");
            e.printStackTrace();
        }
    }

    @Test
    void testConstructor() {
        assertTrue(new Categories().isEmpty());
    }

    @Test
    void testAddCategory() {
        int oldSize = categories.size();
        String selfCareCategory = "Self-care";
        try {
            categories.addCategory(selfCareCategory);
        } catch (NameException e) {
            fail("'" + selfCareCategory + "'category is actually a valid");
            e.printStackTrace();
        }

        assertTrue(categories.contains(selfCareCategory));
        assertEquals(++oldSize, categories.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testAddCategoryThrowNameException(String category) {
        int oldLength = categories.size();
        assertThrows(NameException.class, () -> categories.addCategory(category));
        assertEquals(oldLength, categories.size());
    }

    @Test
    void testRemoveCategory() {
        int oldLength = categories.size();
        try {
            categories.removeCategory(GROCERIES_CATEGORY);
        } catch (NonExistentCategoryException e) {
            fail("'" + GROCERIES_CATEGORY + "' category actually exists in the set");
            e.printStackTrace();
        }
        assertEquals(--oldLength, categories.size());
    }

    @Test
    void testRemoveCategoryThrowNonExistentCategoryException() {
        int oldLength = categories.size();
        assertThrows(NonExistentCategoryException.class,
                () -> categories.removeCategory(String.valueOf(new Random().nextDouble())));
        assertEquals(oldLength, categories.size());
    }

    @Test
    void testContains() {
        assertTrue(categories.contains(TRAVEL_CATEGORY));
        assertTrue(categories.contains(GROCERIES_CATEGORY));
        assertFalse(categories.contains(String.valueOf(new Random().nextDouble())));
    }

    // EFFECTS: populates categories set with categories
    private void initCategories() throws NameException {
        categories.addCategory(GROCERIES_CATEGORY);
        categories.addCategory(TRAVEL_CATEGORY);
    }
}