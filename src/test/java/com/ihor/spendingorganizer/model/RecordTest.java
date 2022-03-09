package com.ihor.spendingorganizer.model;

import com.ihor.spendingorganizer.model.exceptions.NameException;
import com.ihor.spendingorganizer.model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RecordTest {

    private String title;
    private double amount;
    private Category category;
    private Categories categories;
    private Record testRecord;

    @BeforeEach
    void setUp() {
        try {
            title = "Went to SaveOn";
            amount = 80.74;
            categories = new Categories();
            category = new Category("Groceries", categories);
            testRecord = new Record(title, amount, category);
        } catch (NameException e) {
            fail("Title and category are actually acceptable");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail(amount + " amount is actually acceptable");
            e.printStackTrace();
        }
    }

    @Test
    void testConstructor() {
        assertEquals(title, testRecord.getTitle());
        assertEquals(title, testRecord.titleProperty().get());
        assertEquals(amount, testRecord.getAmount());
        assertEquals(amount, testRecord.amountProperty().get());
        assertEquals(category, testRecord.getCategory());
        assertEquals(category, testRecord.categoryProperty().get());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.09, -45})
    void testConstructorThrowNegativeAmountException(double amount) {
        assertThrows(NegativeAmountException.class,
                () -> new Record("Went to SaveOn", amount, category));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testConstructorThrowNameExceptionForTitle(String title) {
        assertThrows(NameException.class, () -> new Record(title, amount, category));
    }

    @ParameterizedTest
    @ValueSource(strings = {"New Title", "   New Title   "})
    void testSetTitle(String title) {
        try {
            testRecord.setTitle(title);
        } catch (NameException e) {
            fail("'" + title + "' title is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(title.trim(), testRecord.getTitle());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testSetTitleThrowNameException(String title) {
        String oldTitle = testRecord.getTitle();
        assertThrows(NameException.class, () -> testRecord.setTitle(title));
        assertEquals(oldTitle, testRecord.getTitle());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 90.7})
    void testSetAmount(double amount) {
        try {
            testRecord.setAmount(amount);
        } catch (NegativeAmountException e) {
            fail(amount + " amount is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(amount, testRecord.getAmount());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.89, -45})
    void testSetAmountThrowNegativeAmountException(double amount) {
        double oldAmount = testRecord.getAmount();
        assertThrows(NegativeAmountException.class, () -> testRecord.setAmount(amount));
        assertEquals(oldAmount, testRecord.getAmount());
    }

    @Test
    void testSetCategory() {
        Category newCategory;
        String name = category.getName() + "...     ";
        try {
            newCategory = new Category(name, categories);
            testRecord.setCategory(newCategory);
            assertEquals(newCategory, testRecord.getCategory());
            assertEquals(3, categories.getCategories().size());
        } catch (NameException e) {
            fail("'" + category + "' category is actually acceptable");
            e.printStackTrace();
        }
    }

    @Test
    void testSetTimeAdded() {
        LocalDateTime time = LocalDateTime.of(2020, Month.DECEMBER, 4, 15, 4);
        testRecord.setTimeAdded(time.toString());
        assertEquals(time, testRecord.getTimeAdded());
        assertEquals(time, testRecord.timeAddedProperty().get());
    }

    @Test
    void testToString() {
        String expected = "Record[" +
                "title=" + testRecord.getTitle() + ", " +
                "amount=" + testRecord.getAmount() + ", " +
                "category=" + testRecord.getCategory() + ", " +
                "timeAdded=" + testRecord.getTimeAdded() + "]";
        assertEquals(expected, testRecord.toString());
    }

    @Test
    void testEqualsReference() {
        Record testRecordRef = testRecord;
        assertEquals(testRecord, testRecordRef);
    }

    @Test
    void testEqualsNullOrDiffClass() {
        assertNotEquals(testRecord, null);
        assertNotEquals(testRecord, new ArrayList<String>());
    }

    @Test
    void testEqualsDiffTitle() {
        Record record;
        try {
            record = new Record(testRecord.getTitle() + "...", testRecord.getAmount(), testRecord.getCategory());
            assertNotEquals(testRecord, record);
        } catch (NameException e) {
            fail("Title is actually valid");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Amount is actually valid");
            e.printStackTrace();
        }
    }

    @Test
    void testEqualsDiffAmount() {
        Record record;
        try {
            record = new Record(testRecord.getTitle(), testRecord.getAmount() + 1, testRecord.getCategory());
            assertNotEquals(testRecord, record);
        } catch (NameException e) {
            fail("Title is actually valid");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Amount is actually valid");
            e.printStackTrace();
        }
    }

    @Test
    void testEqualsDiffCategory() {
        Record newRecord;
        Category newCategory;
        String name = category.getName() + "...     ";
        try {
            newCategory = new Category(name, categories);
            newRecord = new Record(testRecord.getTitle(), testRecord.getAmount(), newCategory);
            assertNotEquals(testRecord, newRecord);
        } catch (NameException e) {
            fail("Title is actually valid");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Amount is actually valid");
            e.printStackTrace();
        }
    }

    @Test
    void testEqualsSameFields() {
        Record record;
        try {
            record = new Record(testRecord.getTitle(), testRecord.getAmount(), testRecord.getCategory());
            record.setTimeAdded(testRecord.getTimeAdded().toString());
            assertEquals(testRecord, record);
        } catch (NameException e) {
            fail("Title is actually valid");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Amount is actually valid");
            e.printStackTrace();
        }
    }
}