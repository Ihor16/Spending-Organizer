package model;

import model.exceptions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpendingListTest {

    private final static String GROCERIES_CATEGORY = "Groceries";
    private final static String CLOTHES_CATEGORY = "Clothes";
    private final static String SELF_CARE_CATEGORY = "Self-care";
    private final static String TRAVEL_CATEGORY = "Travel";
    private SpendingList spendingList;
    private Entry entryHaircut;
    private Entry entryGroceries;

    @BeforeAll
    static void beforeAll() {
        try {
            Categories.addCategory(GROCERIES_CATEGORY);
            Categories.addCategory(CLOTHES_CATEGORY);
            Categories.addCategory(SELF_CARE_CATEGORY);
            Categories.addCategory(TRAVEL_CATEGORY);
        } catch (NameException e) {
            fail("Category names are actually acceptable");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        spendingList = new SpendingList();
        try {
            entryHaircut = new Entry("Got haircut", 31.45, SELF_CARE_CATEGORY);
            entryGroceries = new Entry("Went to SaveOnFoods", 100.76, GROCERIES_CATEGORY);
        } catch (NameException | NegativeAmountException | NonExistentCategoryException e) {
            fail("All entries are actually initialised correctly");
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
            entryTravel = new Entry("Montreal", 599, TRAVEL_CATEGORY);
            spendingList.addEntry(entryTravel);
        } catch (NameException | NegativeAmountException | NonExistentCategoryException e) {
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
            fail("Id actually exists");
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
            newEntry.setCategory(CLOTHES_CATEGORY);
            spendingList.addEntry(newEntry);
        } catch (NameException | NegativeAmountException | NonExistentCategoryException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }

        List<Entry> expectedList = new LinkedList<>(Arrays.asList(newEntry, entryGroceries, entryHaircut));

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
            entryTravel = new Entry("Went to Victoria", 500, TRAVEL_CATEGORY);
            spendingList.addEntry(entryTravel);
        } catch (NameException | NegativeAmountException | NonExistentCategoryException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }

        List<Entry> expectedList = new LinkedList<>(Arrays.asList(entryTravel, entryGroceries, entryHaircut));

        spendingList.sortByAmountSpent();
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByAmountSpentSameAmount() {
        Entry entryTravel = null;
        Entry entryGroceries2 = null;
        try {
            entryTravel = new Entry("Victoria", 545.89, TRAVEL_CATEGORY);
            entryGroceries2 = new Entry("Went to NoFrills", entryGroceries.getAmount(), GROCERIES_CATEGORY);
            spendingList.addEntry(entryTravel);
            spendingList.addEntry(entryGroceries2);
        } catch (NameException | NegativeAmountException | NonExistentCategoryException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }

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
            entryGroceries2 = new Entry("Weekly groceries", 78.8, GROCERIES_CATEGORY);
            spendingList.addEntry(entryGroceries2);
        } catch (NameException | NegativeAmountException | NonExistentCategoryException e) {
            fail("Entry is actually initialised correctly");
            e.printStackTrace();
        }
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
        } catch (NonExistentIdException e) {
            fail("Id is actually correct");
            e.printStackTrace();
        }
        assertEquals(entryGroceries, foundEntry);
    }

    @Test
    void testFindByIdThrowNonExistentIdException() {
        assertThrows(NonExistentIdException.class, () -> spendingList.findById(-1));
    }
}