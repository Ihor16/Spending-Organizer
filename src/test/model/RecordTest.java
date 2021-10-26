package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RecordTest {

    private final String CATEGORY = "Groceries";
    private final String TITLE = "Went to NoFrills";
    private final double AMOUNT = 80.76;
    private Record testRecord;

    @BeforeEach
    void setUp() {
        try {
            testRecord = new Record(TITLE, AMOUNT, CATEGORY);
        } catch (NameException e) {
            fail("Title and category are actually acceptable");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail(AMOUNT + " amount is actually acceptable");
            e.printStackTrace();
        }
    }

    @Test
    void testConstructor() {
        assertEquals(TITLE, testRecord.getTitle());
        assertEquals(AMOUNT, testRecord.getAmount());
        assertEquals(CATEGORY, testRecord.getCategory());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -45, -34.9})
    void testConstructorThrowNegativeAmountException(double amount) {
        assertThrows(NegativeAmountException.class,
                () -> new Record("Went to SaveOn", amount, CATEGORY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testConstructorThrowNameExceptionForTitle(String title) {
        assertThrows(NameException.class, () -> new Record(title, 30, CATEGORY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testConstructorThrowNameExceptionForCategory(String category) {
        assertThrows(NameException.class, () -> new Record("Title", 30, category));
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
        String previousTitle = testRecord.getTitle();
        assertThrows(NameException.class, () -> testRecord.setTitle(title));
        assertEquals(previousTitle, testRecord.getTitle());
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
    @ValueSource(doubles = {0, -45, -34.9})
    void testSetAmountThrowNegativeAmountException(double amount) {
        double oldAmount = testRecord.getAmount();
        assertThrows(NegativeAmountException.class, () -> testRecord.setAmount(amount));
        assertEquals(oldAmount, testRecord.getAmount());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Category", "     Category      "})
    void testSetCategory(String category) {
        try {
            testRecord.setCategory(category);
        } catch (NameException e) {
            fail("'" + category + "' category is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(category.trim(), testRecord.getCategory());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void testSetCategoryThrowNameException(String category) {
        String previousCategory = testRecord.getCategory();
        assertThrows(NameException.class, () -> testRecord.setCategory(category));
        assertEquals(previousCategory, testRecord.getCategory());
    }

    @Test
    void testSetTimeAdded() {
        LocalDateTime time = LocalDateTime.of(2020, Month.DECEMBER, 4, 15, 4);
        testRecord.setTimeAdded(time.toString());
        assertEquals(time, testRecord.getTimeAdded());
    }

    @Test
    void testToString() {
        String expected = "Record[" +
                "title='" + testRecord.getTitle() + "', " +
                "amount=" + testRecord.getAmount() + ", " +
                "category='" + testRecord.getCategory() + "']";
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
        Record record;
        try {
            record = new Record(testRecord.getTitle(), testRecord.getAmount(), testRecord.getCategory() + "...");
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

    @Test
    void testHashCodeSameFields() {
        Record record;
        try {
            record = new Record(testRecord.getTitle(), testRecord.getAmount(), testRecord.getCategory());
            record.setTimeAdded(testRecord.getTimeAdded().toString());
            assertEquals(testRecord.hashCode(), record.hashCode());
        } catch (NameException e) {
            fail("Title is actually valid");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Amount is actually valid");
            e.printStackTrace();
        }
    }
}