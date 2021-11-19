package model;

import model.exceptions.NameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;
    private Categories categories;
    private String name;

    @BeforeEach
    void setUp() {
        name = "Travel";
        try {
            categories = new Categories();
            category = new Category(name, categories);
        } catch (NameException e) {
            fail("Category name is actually acceptable");
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testConstructorThrowNameException(String name) {
        assertThrows(NameException.class, () -> new Category(name, categories));

    }

    @Test
    void testConstructor() {
        assertEquals(name, category.getName());
        assertEquals(name, category.nameProperty().get());
        assertTrue(category.isShown());
        assertTrue(category.isShownProperty().get());
        assertTrue(categories.getCategories().contains(category));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void setNameThrowNameException(String name) {
        assertThrows(NameException.class, () -> category.setName(name, categories));
        assertFalse(categories.getCategories()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList())
                .contains(name));
    }

    @Test
    void setNameSameCategoryAlreadyExists() {
        String newName = "New Category";
        String oldName = category.getName();
        try {
            new Category(newName, categories);
        } catch (NameException e) {
            fail("Category name is actually acceptable");
            e.printStackTrace();
        }
        assertThrows(NameException.class, () -> category.setName(oldName, categories));
        assertTrue(categories.getCategoriesNames().contains(oldName));
        assertEquals(1, categories.getCategoriesNames().stream()
                .filter(s -> s.equals(newName))
                .count());
    }

    @Test
    void setName() {
        String newName = "   " + name + "...";
        try {
            category.setName(newName, categories);
        } catch (NameException e) {
            fail("Category name is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(newName.trim(), category.getName());
        assertTrue(categories.getCategories().contains(category));
    }

    @Test
    void setIsShown() {
        boolean newIsShown = !category.isShown();
        category.setIsShown(newIsShown);
        assertEquals(newIsShown, category.isShown());
        assertTrue(categories.getCategories().contains(category));
    }

    @Test
    void testToString() {
        String expected = new StringJoiner(", ", Category.class.getSimpleName() + "[", "]")
                .add("name=" + category.getName())
                .toString();
        assertEquals(expected, category.toString());
    }

    @Test
    void testEquals() {
        try {
            Category instanceCategory = category;
            Category copyCategory = new Category(category.getName(), categories);

            assertEquals(category, instanceCategory);
            assertEquals(category, copyCategory);
        } catch (NameException e) {
            fail("Category name is actually acceptable");
            e.printStackTrace();
        }
        assertNotEquals(category, null);
        assertNotEquals(category, new ArrayList<>());
    }

    @Test
    void testHashCode() {
        try {
            Category instanceCategory = category;
            Category copyCategory = new Category(category.getName(), categories);

            assertEquals(category.hashCode(), instanceCategory.hashCode());
            assertEquals(category.hashCode(), copyCategory.hashCode());
        } catch (NameException e) {
            e.printStackTrace();
        }
    }
}