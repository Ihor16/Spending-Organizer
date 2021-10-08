package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriesTest {

    private Categories categories;
    private String category1;
    private String category2;

    @BeforeEach
    void setUp() {
        categories = new Categories();
        category1 = "Travel";
        category2 = "Family";
        categories.addCategory(category1);
        categories.addCategory(category2);
    }

    @Test
    void testConstructor() {
        assertTrue(new Categories().isEmpty());
    }

    @Test
    void testAddCategory() {
        assertEquals(2, categories.length());
    }

    @Test
    void testRemoveCategory() {
        categories.removeCategory(category1);
        assertEquals(1, categories.length());
    }

    @Test
    void testContains() {
        assertTrue(categories.contains(category2));
    }
}