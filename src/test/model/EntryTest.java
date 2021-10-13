package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntryTest {

    private Entry testEntry;

    @BeforeEach
    void setUp() {
        testEntry = new Entry("Went to NoFrills", 80, "Groceries");
    }

    @Test
    void testConstructor() {
        assertEquals("Went to NoFrills", testEntry.getTitle());
        assertEquals(80, testEntry.getAmount());
        assertEquals("Groceries", testEntry.getCategory());
    }

    @Test
    void testSetTitle() {
        testEntry.setTitle("New Title");
        assertEquals("New Title", testEntry.getTitle());
    }

    @Test
    void testSetAmount() {
        testEntry.setAmount(90);
        assertEquals(90, testEntry.getAmount());
    }

    @Test
    void testSetCategory() {
        testEntry.setCategory("Clothes");
        assertEquals("Clothes", testEntry.getCategory());
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