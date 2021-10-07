package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriesTest {

    Categories categories;

    @BeforeEach
    void setUp() {
        categories = new Categories();
    }

    @Test
    void testConstructor() {
        assertTrue(categories.isEmpty());
    }

    @Test
    void testAddCategory() {
        categories.addCategory("Travel");
        categories.addCategory("Family");
        assertEquals(2, categories.getCategories().size());
    }

    @Test
    void testRemoveCategory() {
        categories.addCategory("Travel");
        categories.addCategory("Family");

        categories.removeCategory("Travel");
        assertEquals(1, categories.length());
    }
}