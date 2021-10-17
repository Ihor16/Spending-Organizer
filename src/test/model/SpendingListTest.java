package model;

import model.exceptions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpendingListTest {

    private SpendingList spendingList;
    private Entry entryHaircut;
    private Entry entryGroceries;

    @BeforeAll
    static void beforeAll() {
        try {
            Categories.addCategory("Groceries");
            Categories.addCategory("Self-care");
            Categories.addCategory("Travel");
        } catch (NameException e) {
            fail("Shouldn't throw any exceptions");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        spendingList = new SpendingList();

        try {
            entryHaircut = new Entry("Got haircut", 31.45, "Self-care");
            entryGroceries = new Entry("Went to SaveOnFoods", 100.76, "Groceries");
        } catch (NameException | NegativeAmountException | NotFoundCategoryException e) {
            fail();
            e.printStackTrace();
        }

        spendingList.addEntry(entryHaircut);
        spendingList.addEntry(entryGroceries);
    }

    @Test
    void testConstructor() {
        SpendingList newList = new SpendingList();
        assertTrue(newList.isEmpty());
    }

    @Test
    void testAddEntry() {
        Entry entryTravel = null;

        try {
            entryTravel = new Entry("Montreal", 599, "Travel");
        } catch (NameException | NegativeAmountException | NotFoundCategoryException e) {
            fail();
            e.printStackTrace();
        }

        spendingList.addEntry(entryTravel);
        assertEquals(3, spendingList.length());
        assertEquals(entryTravel, spendingList.getSpendingList().get(0));
    }

    @Test
    void testRemoveEntry() {
        try {
            assertTrue(spendingList.removeById(entryGroceries.getId()));
        } catch (WrongIdException e) {
            fail("Id actually exists");
            e.printStackTrace();
        }
        assertEquals(1, spendingList.length());
    }

    @Test
    void testRemoveEntryThrowWrongIdException() {
        int oldLength = spendingList.length();
        assertThrows(WrongIdException.class, () -> spendingList.removeById(-1));
        assertEquals(oldLength, spendingList.length());
    }

    @Test
    void testSortByDateAddedEmptyList() {
        SpendingList newList = new SpendingList();
//        try {
//            Entry entry = new Entry("Name title", 35, "Travel");
//            newList.addEntry(entry);
//        } catch (WrongNameException | NegativeAmountException | NotFoundCategoryException e) {
//            e.printStackTrace();
//        }
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
        Entry entryTravel = null;
        try {
            entryTravel = new Entry("Went to Victoria", 500, "Travel");
        } catch (NameException | NegativeAmountException | NotFoundCategoryException e) {
            e.printStackTrace();
        }
        spendingList.addEntry(entryTravel);
        spendingList.sortByAmountSpent();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryTravel);
        expectedList.add(entryGroceries);
        expectedList.add(entryHaircut);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByAmountSpentSameAmount() {
        Entry entryTravel = null;
        Entry entryGroceries2 = null;
        try {
            entryTravel = new Entry("Victoria", 545.89, "Travel");
            entryGroceries2 = new Entry("Went to NoFrills", entryGroceries.getAmount(), "Travel");
        } catch (NameException | NegativeAmountException | NotFoundCategoryException e) {
            fail();
            e.printStackTrace();
        }

        spendingList.addEntry(entryTravel);
        spendingList.addEntry(entryGroceries2);
        spendingList.sortByAmountSpent();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryTravel);
        expectedList.add(entryGroceries2);
        expectedList.add(entryGroceries);
        expectedList.add(entryHaircut);

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
        expectedList.add(entryHaircut);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByCategorySameCategory() {
        Entry entryGroceries2 = null;
        try {
            entryGroceries2 = new Entry("Weekly groceries", 78.8, entryGroceries.getCategory());
        } catch (EntryFieldException e) {
            fail("Shouldn't throw anything");
            e.printStackTrace();
        }
        spendingList.addEntry(entryGroceries2);
        spendingList.sortByCategory();

        List<Entry> expectedList = new LinkedList<>();
        expectedList.add(entryGroceries2);
        expectedList.add(entryGroceries);
        expectedList.add(entryHaircut);

        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testFindById() {
        Entry foundEntry = null;
        try {
            foundEntry = spendingList.findById(entryGroceries.getId());
        } catch (WrongIdException e) {
            fail("Id is actually correct");
            e.printStackTrace();
        }
        assertEquals(entryGroceries, foundEntry);
    }

    @Test
    void testFindByIdThrowWrongIdException() {
        assertThrows(WrongIdException.class, () -> spendingList.findById(-1));
    }
}