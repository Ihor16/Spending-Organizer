package model;

import model.exceptions.NegativeAmountException;
import model.exceptions.NonExistentCategoryException;
import model.exceptions.NameException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EntryTest {

    private final static String GROCERIES_CATEGORY = "Groceries";
    private final static String CLOTHES_CATEGORY = "Clothes";
    private final String ENTRY_TITLE = "Went to NoFrills";
    private final double ENTRY_AMOUNT = 80.76;
    private Entry testEntry;

    @BeforeAll
    static void beforeAll() {
        try {
            Categories.addCategory(GROCERIES_CATEGORY);
            Categories.addCategory(CLOTHES_CATEGORY);
        } catch (NameException e) {
            fail("Names of categories are actually acceptable");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        try {
            testEntry = new Entry(ENTRY_TITLE, ENTRY_AMOUNT, GROCERIES_CATEGORY);
        } catch (NameException e) {
            fail("Title and category names are actually acceptable");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail(ENTRY_AMOUNT + " amount is actually acceptable");
            e.printStackTrace();
        } catch (NonExistentCategoryException e) {
            fail("'" + GROCERIES_CATEGORY  + "' category actually exists");
            e.printStackTrace();
        }
    }

    @Test
    void testConstructor() {
        assertEquals(ENTRY_TITLE, testEntry.getTitle());
        assertEquals(ENTRY_AMOUNT, testEntry.getAmount());
        assertEquals(GROCERIES_CATEGORY, testEntry.getCategory());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -45, -34.9})
    void testConstructorThrowNegativeAmountException(double amount) {
        assertThrows(NegativeAmountException.class,
                () -> new Entry("Went to SaveOn", amount, "Groceries"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testConstructorThrowNameExceptionForTitle(String title) {
        assertThrows(NameException.class, () -> new Entry(title, 30, "Groceries"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testConstructorThrowNameExceptionForCategory(String category) {
        assertThrows(NameException.class, () -> new Entry("Title", 30, category));
    }

    @ParameterizedTest
    @ValueSource(strings = {"My Category"})
    void testConstructorThrowNonExistentCategoryException(String category) {
        assertThrows(NonExistentCategoryException.class, () -> new Entry("Title", 30, category));
    }

    @ParameterizedTest
    @ValueSource(strings = {"New Title", "   New Title   "})
    void testSetTitle(String title) {
        try {
            testEntry.setTitle(title);
        } catch (NameException e) {
            fail("'" + title + "' title is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(title.trim(), testEntry.getTitle());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testSetTitleThrowNameException(String title) {
        String previousTitle = testEntry.getTitle();
        assertThrows(NameException.class, () -> testEntry.setTitle(title));
        assertEquals(previousTitle, testEntry.getTitle());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 90.7})
    void testSetAmount(double amount) {
        try {
            testEntry.setAmount(amount);
        } catch (NegativeAmountException e) {
            fail(amount + " amount is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(amount, testEntry.getAmount());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -45, -34.9})
    void testSetAmountThrowNegativeAmountException(double amount) {
        double oldAmount = testEntry.getAmount();
        assertThrows(NegativeAmountException.class, () -> testEntry.setAmount(amount));
        assertEquals(oldAmount, testEntry.getAmount());
    }

    @ParameterizedTest
    @ValueSource(strings = {GROCERIES_CATEGORY, CLOTHES_CATEGORY + "      "})
    void testSetCategory(String category) {
        try {
            testEntry.setCategory(category);
        } catch (NonExistentCategoryException e) {
            fail("'" + category + "' category actually exists");
            e.printStackTrace();
        } catch (NameException e) {
            fail("'" + category + "' name is acceptable");
            e.printStackTrace();
        }
        assertEquals(category.trim(), testEntry.getCategory());
    }

    @Test
    void testSetCategoryThrowNonExistentCategoryException() {
        String oldCategory = testEntry.getCategory();
        assertThrows(NonExistentCategoryException.class, () -> testEntry.setCategory("My category"));
        assertEquals(oldCategory, testEntry.getCategory());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testSetCategoryThrowNameException(String category) {
        String oldCategory = testEntry.getCategory();
        assertThrows(NameException.class, () -> testEntry.setCategory(category));
        assertEquals(oldCategory, testEntry.getCategory());
    }

    @Test
    void testToString() {
        String expected = "Entry[id=" + testEntry.getId() + ", " +
                "title='" + testEntry.getTitle() + "', " +
                "amount=" + testEntry.getAmount() + ", " +
                "category='" + testEntry.getCategory() + "']";
        assertEquals(expected, testEntry.toString());
    }
}