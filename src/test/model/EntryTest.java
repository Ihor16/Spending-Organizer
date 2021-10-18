package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EntryTest {

    private final String CATEGORY = "Groceries";
    private final String TITLE = "Went to NoFrills";
    private final double AMOUNT = 80.76;
    private Entry testEntry;

    @BeforeEach
    void setUp() {
        try {
            testEntry = new Entry(TITLE, AMOUNT, CATEGORY);
        } catch (NameException e) {
            fail("Title name is actually acceptable");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail(AMOUNT + " amount is actually acceptable");
            e.printStackTrace();
        }
    }

    @Test
    void testConstructor() {
        assertEquals(TITLE, testEntry.getTitle());
        assertEquals(AMOUNT, testEntry.getAmount());
        assertEquals(CATEGORY, testEntry.getCategory());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -45, -34.9})
    void testConstructorThrowNegativeAmountException(double amount) {
        assertThrows(NegativeAmountException.class,
                () -> new Entry("Went to SaveOn", amount, CATEGORY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testConstructorThrowNameExceptionForTitle(String title) {
        assertThrows(NameException.class, () -> new Entry(title, 30, CATEGORY));
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

    @Test
    void testSetCategory() {
        String category = "Travel";
        testEntry.setCategory(category);
        assertEquals(category, testEntry.getCategory());
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