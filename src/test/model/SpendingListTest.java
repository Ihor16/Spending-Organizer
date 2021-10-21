package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpendingListTest {

    private final String GROCERIES_CATEGORY = "Groceries";
    private final String TRAVEL_CATEGORY = "Travel";
    private SpendingList spendingList;
    private Entry entryTravel;
    private Entry entryGroceries;
    private Entry notAddedEntry;

    @BeforeEach
    void setUp() {
        spendingList = new SpendingList();
        initEntries();
        spendingList.addEntry(entryTravel);
        spendingList.addEntry(entryGroceries);
    }

    @Test
    void testConstructor() {
        assertTrue(new SpendingList().isEmptyList());
        assertTrue(new SpendingList().getCategories().isEmpty());
    }

    @Test
    void testAddEntrySameCategory() {
        int previousListSize = spendingList.sizeOfList();
        try {
            notAddedEntry.setCategory(TRAVEL_CATEGORY);
        } catch (NameException e) {
            fail("Category name is actually fine");
        }
        spendingList.addEntry(notAddedEntry);

        assertEquals(previousListSize + 1, spendingList.sizeOfList());
        assertEquals(notAddedEntry, spendingList.getSpendingList().get(0));
        assertTrue(spendingList.getCategories().contains(TRAVEL_CATEGORY));
        assertEquals(previousListSize, spendingList.sizeOfSet());
    }

    @Test
    void testAddEntryNewCategory() {
        int previousListSize = spendingList.sizeOfList();
        spendingList.addEntry(notAddedEntry);

        assertEquals(previousListSize + 1, spendingList.sizeOfList());
        assertEquals(notAddedEntry, spendingList.getSpendingList().get(0));
        assertTrue(spendingList.getCategories().contains(notAddedEntry.getCategory()));
        assertEquals(previousListSize + 1, spendingList.sizeOfSet());
    }

    @ParameterizedTest
    @ValueSource(strings = {"New Category", "        New Category       "})
    void testAddCategoryNewCategory(String category) {
        int previousSetSize = spendingList.sizeOfSet();
        try {
            spendingList.addCategory(category);
        } catch (NameException e) {
            fail("Category is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(previousSetSize + 1, spendingList.sizeOfSet());
        assertTrue(spendingList.getCategories().contains(category.trim()));
    }

    @Test
    void testAddCategorySameCategory() {
        int previousSetSize = spendingList.sizeOfSet();
        try {
            spendingList.addCategory(GROCERIES_CATEGORY);
            spendingList.addCategory(GROCERIES_CATEGORY + "      ");
        } catch (NameException e) {
            fail("Category is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(previousSetSize, spendingList.sizeOfSet());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "        "})
    void testAddCategoryThrowNameException(String category) {
        assertThrows(NameException.class, () -> spendingList.addCategory(category));
    }

    @Test
    void testRemoveEntry() {
        assertTrue(spendingList.removeEntry(entryGroceries));
        assertEquals(1, spendingList.sizeOfList());
    }

    @Test
    void testRemoveCategory() {
        assertTrue(spendingList.removeCategory(entryGroceries.getCategory()));
        assertFalse(spendingList.getCategories().contains(entryGroceries.getCategory()));
    }

    @Test
    void testGetEntry() {
        assertEquals(spendingList.getSpendingList().get(0), spendingList.getEntry(0));
    }

    @Test
    void testSortEmptyList() {
        SpendingList newList = new SpendingList();
        newList.sortByDate();
        assertTrue(newList.isEmptyList());

        newList.sortByCategory();
        assertTrue(newList.isEmptyList());

        newList.sortByAmountSpent();
        assertTrue(newList.isEmptyList());
    }

    @Test
    void testSortByDate() {
        List<Entry> expectedList = spendingList.getSpendingList();
        spendingList.sortByDate();
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByDateManuallyAddNewEntry() {
        Entry manualEntry = null;
        try {
            manualEntry = new Entry();
            manualEntry.setTitle("Jeans");
            manualEntry.setAmount(560);
            manualEntry.setCategory("Clothes");
            spendingList.addEntry(manualEntry);
        } catch (NegativeAmountException e) {
            fail("Amount is actually acceptable");
            e.printStackTrace();
        } catch (NameException e) {
            fail("Title and category are actually acceptable");
            e.printStackTrace();
        }
        List<Entry> expectedList = Arrays.asList(manualEntry, entryGroceries, entryTravel);
        spendingList.sortByDate();
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByAmountSpent() {
        spendingList.addEntry(notAddedEntry);
        List<Entry> expectedList = Arrays.asList(notAddedEntry, entryTravel, entryGroceries);
        spendingList.sortByAmountSpent();
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByAmountSpentSameAmount() {
        try {
            notAddedEntry.setAmount(entryGroceries.getAmount());
            spendingList.addEntry(notAddedEntry);
        } catch (NegativeAmountException e) {
            fail("Amount is actually acceptable");
        }
        spendingList.sortByAmountSpent();
        List<Entry> expectedList = Arrays.asList(entryTravel, notAddedEntry, entryGroceries);
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByCategory() {
        spendingList.sortByCategory();
        List<Entry> expectedList = Arrays.asList(entryGroceries, entryTravel);
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    @Test
    void testSortByCategorySameCategory() {
        try {
            notAddedEntry.setCategory(TRAVEL_CATEGORY);
            spendingList.addEntry(notAddedEntry);
        } catch (NameException e) {
            fail("Category name is actually acceptable");
        }
        spendingList.sortByCategory();
        List<Entry> expectedList = Arrays.asList(entryGroceries, notAddedEntry, entryTravel);
        assertEquals(expectedList, spendingList.getSpendingList());
    }

    // EFFECTS: inits test entries
    private void initEntries() {
        try {
            entryTravel = new Entry("Went to Toronto", 401.34, TRAVEL_CATEGORY);
            Thread.sleep(10);
            entryGroceries = new Entry("Went to SaveOnFoods", 100.76, GROCERIES_CATEGORY);
            Thread.sleep(10);
            notAddedEntry = new Entry("Other entry",
                    Math.max(entryTravel.getAmount(), entryGroceries.getAmount()) * 10,
                    "Ze New Category");
        } catch (NameException | NegativeAmountException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}