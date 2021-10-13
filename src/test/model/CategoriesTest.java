package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        String category3 = "Self-care";
        categories.addCategory(category3);
        assertEquals(3, categories.length());
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

    @Test
    void testGetCategories() {
        assertEquals(new HashSet<>(Arrays.asList(category1, category2)), categories.getCategories());
    }

    @Test
    void testToString() {
        assertEquals(String.join(", ", category1, category2), categories.toString());
    }

    @Test
    void testToStringEmpty() {
        assertEquals("", new Categories().toString());
    }
}