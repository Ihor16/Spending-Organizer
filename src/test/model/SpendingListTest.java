package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpendingListTest {

    private SpendingList spendingList;
    private Entry entrySelfCare;
    private Entry entryGroceries;

    @BeforeEach
    void setUp() {
        spendingList = new SpendingList();

        entrySelfCare = new Entry("Got haircut", 31.45, "Self-care");
        entryGroceries = new Entry("Went to SaveOnFoods", 100.76, "Groceries");

        spendingList.addEntry(entrySelfCare);
        spendingList.addEntry(entryGroceries);
    }

    @Test
    void testConstructor() {
        SpendingList newList = new SpendingList();
        assertTrue(newList.isEmpty());
    }

    @Test
    void testAddEntry() {
        assertEquals(2, spendingList.getSpendingList().size());
        assertEquals(entryGroceries, spendingList.getSpendingList().get(0));
    }

    @Test
    void testRemoveEntry() {
        assertTrue(spendingList.removeById(entryGroceries.getId()));
        assertFalse(spendingList.removeById(45));
        assertEquals(1, spendingList.length());
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
    void testSortByAmountSpentEmptyList() {
        SpendingList newList = new SpendingList();
        newList.sortByAmountSpent();
        assertTrue(newList.isEmpty());
    }

    @Test
    void testSortByAmountSpent() {
        Entry entryTravel = new Entry("Went to Victoria", 500, "Travel");
        spendingList.addEntry(entryTravel);
        spendingList.sortByAmountSpent();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryTravel);
        expectedList.add(entryGroceries);
        expectedList.add(entrySelfCare);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByAmountSpentSameAmount() {
        Entry entryTravel = new Entry("Victoria", 545.89, "Travel");
        Entry entryGroceries2 = new Entry("Went to NoFrills", 100.76, "Travel");
        spendingList.addEntry(entryTravel);
        spendingList.addEntry(entryGroceries2);
        spendingList.sortByAmountSpent();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryTravel);
        expectedList.add(entryGroceries2);
        expectedList.add(entryGroceries);
        expectedList.add(entrySelfCare);

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
        expectedList.add(entrySelfCare);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByCategorySameCategory() {
        Entry entryGroceries2 = new Entry("Weekly groceries", 78.8, "Groceries");
        spendingList.addEntry(entryGroceries2);
        spendingList.sortByCategory();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryGroceries2);
        expectedList.add(entryGroceries);
        expectedList.add(entrySelfCare);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testFindById() {
        Entry foundEntry = spendingList.findById(entryGroceries.getId());
        assertEquals(entryGroceries, foundEntry);
    }

    @Test
    void testIsValidId() {
        assertTrue(spendingList.isValidId(entrySelfCare.getId()));
        assertFalse(spendingList.isValidId(45));
    }
}