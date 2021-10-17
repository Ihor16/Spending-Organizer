package model;

import model.exceptions.NotFoundCategoryException;
import model.exceptions.NameException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriesTest {

    private String category1 = "Travel";
    private String category2 = "Groceries";

    @Order(1)
    @Test
    void testConstructor() {
        assertTrue(Categories.isEmpty());
    }

    @Order(2)
    @Test
    void testAddCategory() {
        try {
            Categories.addCategory(category1);
        } catch (NameException e) {
            fail("Shouldn't throw any exceptions");
            e.printStackTrace();
        }
        assertEquals(1, Categories.length());
    }

    @Order(3)
    @Test
    void testAddCategoryThrowWrongCategoryException() {
        String category3 = "";
        int oldLength = Categories.length();
        assertThrows(NameException.class, () -> Categories.addCategory(category3));
        assertEquals(oldLength, Categories.length());
    }

    @Order(4)
    @Test
    void testRemoveCategory() {
        int oldLength = Categories.length();
        try {
            Categories.removeCategory(category1);
        } catch (NotFoundCategoryException e) {
            fail("Shouldn't throw any exceptions");
            e.printStackTrace();
        }
        assertEquals(--oldLength, Categories.length());
    }

    @Order(5)
    @Test
    void testRemoveCategoryThrowWrongCategoryException() {
        int oldLength = Categories.length();
        assertThrows(NotFoundCategoryException.class, () -> Categories.removeCategory(""));
        assertEquals(oldLength, Categories.length());
    }

    @Order(6)
    @Test
    void testContains() {
        try {
            Categories.addCategory(category1);
        } catch (NameException e) {
            fail("Shouldn't throw any exceptions");
            e.printStackTrace();
        }
        assertTrue(Categories.contains(category1));
        assertFalse(Categories.contains(category2));
    }

    @Order(7)
    @Test
    void testGetCategories() {
        assertEquals(new HashSet<>(Collections.singletonList(category1)), Categories.getCategories());
    }
}