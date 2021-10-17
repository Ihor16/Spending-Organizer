package model;

import model.exceptions.NegativeAmountException;
import model.exceptions.NotFoundCategoryException;
import model.exceptions.NameException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntryTest {

    private Entry testEntry;

    @BeforeAll
    static void beforeAll() {
        try {
            Categories.addCategory("Groceries");
            Categories.addCategory("Clothes");
        } catch (NameException e) {
            fail("Shouldn't throw any exceptions");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        try {
            testEntry = new Entry("Went to NoFrills", 80, "Groceries");
        } catch (NameException | NegativeAmountException | NotFoundCategoryException e) {
            fail("Shouldn't throw any exceptions");
            e.printStackTrace();
        }
    }

    @Test
    void testConstructor() {
        assertEquals("Went to NoFrills", testEntry.getTitle());
        assertEquals(80, testEntry.getAmount());
        assertEquals("Groceries", testEntry.getCategory());
    }

    @Test
    void testConstructorThrowNegativeAmountException() {
        assertThrows(NegativeAmountException.class,
                () -> new Entry("Went to SaveOn", 0, "Groceries"));
        assertThrows(NegativeAmountException.class,
                () -> new Entry("Went to SaveOn", -1, "Groceries"));
    }

    @Test
    void testConstructorThrowWrongTitleException() {
        assertThrows(NameException.class,
                () -> new Entry("", 30, "Groceries"));
    }

    @Test
    void testSetTitle() {
        try {
            testEntry.setTitle("New Title");
        } catch (NameException e) {
            fail("Name is actually right");
            e.printStackTrace();
        }
        assertEquals("New Title", testEntry.getTitle());
    }

    @Test
    void testSetTitleThrowWrongTitleException() {
        String previousTitle = testEntry.getTitle();
        assertThrows(NameException.class, () -> testEntry.setTitle(""));
        assertThrows(NameException.class, () -> testEntry.setTitle("      "));
        assertEquals(previousTitle, testEntry.getTitle());
    }

    @Test
    void testSetAmount() {
        try {
            testEntry.setAmount(90);
        } catch (NegativeAmountException e) {
            fail("Amount is actually ok");
            e.printStackTrace();
        }
        assertEquals(90, testEntry.getAmount());
    }

    @Test
    void testSetAmountThrowsNegativeAmountException() {
        double oldAmount = testEntry.getAmount();
        assertThrows(NegativeAmountException.class, () -> testEntry.setAmount(-90));
        assertThrows(NegativeAmountException.class, () -> testEntry.setAmount(0));
        assertEquals(oldAmount, testEntry.getAmount());
    }

    @Test
    void testSetCategory() {
        try {
            testEntry.setCategory("Clothes");
        } catch (NotFoundCategoryException | NameException e) {
            e.printStackTrace();
        }
        assertEquals("Clothes", testEntry.getCategory());
    }

    @Test
    void testSetCategoryThrowNotFoundCategoryException() {
        String oldCategory = testEntry.getCategory();
        assertThrows(NotFoundCategoryException.class, () -> testEntry.setCategory("My category"));
        assertEquals(oldCategory, testEntry.getCategory());
    }

    @Test
    void testSetCategoryThrowWrongNameException() {
        String oldCategory = testEntry.getCategory();
        assertThrows(NameException.class, () -> testEntry.setCategory(""));
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