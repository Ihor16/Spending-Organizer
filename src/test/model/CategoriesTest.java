package model;

import javafx.collections.FXCollections;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CategoriesTest {

    private SpendingList spendingList;

    private Record record;
    private String recordTitle;
    private double recordAmount;
    private Category recordCategory;

    private Categories categories;
    private String categoryName;

    @BeforeEach
    void setUp() {
        recordTitle = "Went to Victoria";
        recordAmount = 590.83;
        categoryName = "Travel";
        categories = new Categories();

        try {
            recordCategory = new Category(categoryName, categories);
        } catch (NameException e) {
            fail("Not the case: " + e.getMessage());
        }

        try {
            record = new Record(recordTitle, recordAmount, recordCategory);
        } catch (NameException | NegativeAmountException e) {
            e.printStackTrace();
        }

        spendingList = new SpendingList(categories);
        spendingList.addRecord(record);
    }

    @Test
    void testConstructor() {
        assertEquals(1, new Categories().getCategories().size());
    }

    @Test
    void testSetCategoriesEmptySet() {
        categories.setCategories(FXCollections.observableArrayList());
        assertEquals(1, categories.getCategories().size());
    }

    @Test
    void testSetCategories() {
        try {
            Category newCategory = new Category(categoryName + "...", categories);
            Category newCategory2 = new Category(categoryName + "xxx", categories);
            categories.setCategories(FXCollections.observableArrayList(newCategory, newCategory2));
            assertEquals(3, categories.getCategories().size());
        } catch (NameException e) {
            fail("Not the case: " + e.getMessage());
        }
    }

    @Test
    void testGetCategoriesNames() {
        assertTrue(categories.getCategoriesNames().contains(categories.getDefaultCategory().getName()));
        assertEquals(2, categories.getCategoriesNames().size());
    }

    @Test
    void testGetCategoriesNamesAddNewCategories() {
        String newCategoryName = categoryName + "...";
        String newCategoryName2 = categoryName + "xxx";
        try {
            new Category(newCategoryName, categories);
            new Category(newCategoryName2, categories);
            assertTrue(categories.getCategoriesNames().contains(newCategoryName));
            assertTrue(categories.getCategoriesNames().contains(newCategoryName2));
        } catch (NameException e) {
            fail("Not the case: " + e.getMessage());
        }
    }

    @Test
    void testGetCategoryByNameNotExistingCategory() {
        Category foundCategory = categories.getCategoryByName(null);
        assertEquals(categories.getDefaultCategory(), foundCategory);
    }

    @Test
    void testGetCategoryByName() {
        Category foundCategory = categories.getCategoryByName(recordCategory.getName());
        assertEquals(recordCategory, foundCategory);
    }

    @Test
    void addExistingCategory() {
        int oldSize = categories.getCategories().size();
        categories.add(recordCategory);
        assertEquals(oldSize, categories.getCategories().size());
    }

    @Test
    void addNewCategory() {
        int oldSize = categories.getCategories().size();
        try {
            Category newCategory = new Category(categoryName + "...", categories);
            assertEquals(oldSize + 1, categories.getCategories().size());
            assertTrue(categories.getCategories().contains(newCategory));
            assertTrue(spendingList.getCategories().getCategories().contains(newCategory));
        } catch (NameException e) {
            fail("Not the case: " + e.getMessage());
        }
    }

    @Test
    void removeUndefinedCategory() {
        int oldSize = categories.getCategories().size();
        categories.remove(categories.getDefaultCategory(), spendingList);
        assertEquals(oldSize, categories.getCategories().size());
    }

    @Test
    void removeExistingCategory() {
        categories.remove(recordCategory, spendingList);

        assertFalse(spendingList.getCategories().getCategories().contains(recordCategory));
        assertTrue(spendingList.getCategories().getCategories().contains(categories.getDefaultCategory()));
        assertFalse(categories.getCategories().contains(recordCategory));
    }

    @Test
    void testSetDefaultCategorySameName() {
        try {
            categories.setDefaultCategory(new Category(categories.getDefaultCategory().getName(),
                    categories, true, true));
            assertEquals(spendingList.getCategories(), categories);
        } catch (NameException e) {
            fail("Not the case: " + e.getMessage());
        }
    }

    @Test
    void testSetDefaultCategory() {
        try {
            categories.setDefaultCategory(new Category("new default", categories, true, true));
            assertEquals(spendingList.getCategories(), categories);
        } catch (NameException e) {
            fail("Not the case: " + e.getMessage());
        }
    }

    @Test
    void testEquals() {
        Categories instanceCategory = categories;
        Categories copyCategories = new Categories();
        copyCategories.setCategories(Collections.singletonList(recordCategory));

        assertEquals(categories, instanceCategory);
        assertEquals(categories, copyCategories);
        assertNotEquals(categories, null);
        assertNotEquals(categories, new ArrayList<>());
    }
}