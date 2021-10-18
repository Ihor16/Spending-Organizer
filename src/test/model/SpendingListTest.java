package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import model.exceptions.NonExistentIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpendingListTest {

    private final String GROCERIES_CATEGORY = "Groceries";
    private final String TRAVEL_CATEGORY = "Travel";
    private SpendingList spendingList;
    private Entry entryTravel;
    private Entry entryGroceries;

    @BeforeEach
    void setUp() {
        spendingList = new SpendingList();
        try {
            initEntries();
        } catch (NegativeAmountException | NameException e) {
            fail("All entries are actually initialised correctly");
            e.printStackTrace();
        }
        spendingList.addEntry(entryTravel);
        spendingList.addEntry(entryGroceries);
    }

    @Test
    void testConstructor() {
        assertTrue(new SpendingList().isEmpty());
    }

    @Test
    void testAddEntry() {
        Entry entryTravel = null;
        try {
            entryTravel = new Entry("Montreal", 599, TRAVEL_CATEGORY);
            spendingList.addEntry(entryTravel);
        } catch (NameException | NegativeAmountException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }
        assertEquals(3, spendingList.length());
        assertEquals(entryTravel, spendingList.getSpendingList().get(0));
    }

    @Test
    void testRemoveEntry() {
        try {
            assertTrue(spendingList.removeById(entryGroceries.getId()));
        } catch (NonExistentIdException e) {
            fail("Element with id " + entryGroceries.getId() + " actually exists");
            e.printStackTrace();
        }
        assertEquals(1, spendingList.length());
    }

    @Test
    void testRemoveEntryThrowNonExistentIdException() {
        int oldLength = spendingList.length();
        assertThrows(NonExistentIdException.class, () -> spendingList.removeById(-1));
        assertEquals(oldLength, spendingList.length());
    }

    @Test
    void testSortByDateAddedEmptyList() {
        SpendingList newList = new SpendingList();
        newList.sortByDate();
        assertTrue(newList.isEmpty());
    }

    @Test
    void testSortByDateAdded() {
        List<Entry> expectedList = spendingList.getSpendingList();
        spendingList.sortByDate();
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByDateAddedManuallyAddNewEntry() {
        Entry newEntry = null;
        try {
            newEntry = new Entry();
            newEntry.setTitle("Jeans");
            newEntry.setAmount(560);
            newEntry.setCategory("Clothes");
            spendingList.addEntry(newEntry);
        } catch (NameException | NegativeAmountException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(newEntry);
        expectedList.add(entryGroceries);
        expectedList.add(entryTravel);

        spendingList.sortByDate();
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByAmountSpentEmptyList() {
        SpendingList newList = new SpendingList();
        newList.sortByAmountSpent();
        assertTrue(newList.isEmpty());
    }

    @Test
    void testSortByAmountSpent() {
        Entry entryExpensiveTravel = null;
        try {
            entryExpensiveTravel = new Entry("Victoria", entryTravel.getAmount() + 200, TRAVEL_CATEGORY);
            spendingList.addEntry(entryExpensiveTravel);
        } catch (NameException | NegativeAmountException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryExpensiveTravel);
        expectedList.add(entryTravel);
        expectedList.add(entryGroceries);

        spendingList.sortByAmountSpent();
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByAmountSpentSameAmount() {
        Entry entryGroceries2 = null;
        try {
            entryGroceries2 = new Entry("Went to NoFrills", entryGroceries.getAmount(), GROCERIES_CATEGORY);
            spendingList.addEntry(entryGroceries2);
        } catch (NameException | NegativeAmountException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }

        spendingList.sortByAmountSpent();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryTravel);
        expectedList.add(entryGroceries2);
        expectedList.add(entryGroceries);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByCategoryEmptyList() {
        SpendingList newList = new SpendingList();
        newList.sortByCategory();
        assertTrue(newList.isEmpty());
    }

    @Test
    void testSortByCategory() {
        spendingList.sortByCategory();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryGroceries);
        expectedList.add(entryTravel);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByCategorySameCategory() {
        Entry entryGroceries2 = null;
        try {
            entryGroceries2 = new Entry("Weekly groceries", 78.8, GROCERIES_CATEGORY);
            spendingList.addEntry(entryGroceries2);
        } catch (NameException | NegativeAmountException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }
        spendingList.sortByCategory();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryGroceries2);
        expectedList.add(entryGroceries);
        expectedList.add(entryTravel);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testFindById() {
        Entry foundEntry = null;
        try {
            foundEntry = spendingList.findById(entryGroceries.getId());
        } catch (NonExistentIdException e) {
            fail("Element with id " + entryGroceries.getId() + " actually exists");
            e.printStackTrace();
        }
        assertEquals(entryGroceries, foundEntry);
    }

    @Test
    void testFindByIdThrowNonExistentIdException() {
        assertThrows(NonExistentIdException.class, () -> spendingList.findById(-1));
    }

    private void initEntries() throws NegativeAmountException, NameException {
        entryTravel = new Entry("Went to Toronto", 401.34, TRAVEL_CATEGORY);
        entryGroceries = new Entry("Went to SaveOnFoods", 100.76, GROCERIES_CATEGORY);
    }
}