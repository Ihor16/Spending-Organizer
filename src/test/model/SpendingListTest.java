package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpendingListTest {

    private final String GROCERIES_CATEGORY = "Groceries";
    private final String TRAVEL_CATEGORY = "Travel";
    private SpendingList spendingList;
    private Record recordTravel;
    private Record recordGroceries;
    private Record notAddedRecord;

    @BeforeEach
    void setUp() {
        spendingList = new SpendingList();
        initRecords();
        spendingList.addRecord(recordTravel);
        spendingList.addRecord(recordGroceries);
    }

    @Test
    void testConstructor() {
        assertTrue(new SpendingList().isEmptyList());
        assertTrue(new SpendingList().getCategories().isEmpty());
    }

    @Test
    void testAddRecordSameCategory() {
        int previousListSize = spendingList.sizeOfList();
        try {
            notAddedRecord.setCategory(TRAVEL_CATEGORY);
        } catch (NameException e) {
            fail("Category name is actually fine");
        }
        spendingList.addRecord(notAddedRecord);

        assertEquals(previousListSize + 1, spendingList.sizeOfList());
        assertEquals(notAddedRecord, spendingList.getRecords().get(0));
        assertTrue(spendingList.getCategories().contains(TRAVEL_CATEGORY));
        assertEquals(previousListSize, spendingList.sizeOfSet());
    }

    @Test
    void testAddRecordNewCategory() {
        int previousListSize = spendingList.sizeOfList();
        spendingList.addRecord(notAddedRecord);

        assertEquals(previousListSize + 1, spendingList.sizeOfList());
        assertEquals(notAddedRecord, spendingList.getRecords().get(0));
        assertTrue(spendingList.getCategories().contains(notAddedRecord.getCategory()));
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
    void testRemoveRecord() {
        int previousSetSize = spendingList.sizeOfSet();
        assertTrue(spendingList.removeRecord(recordGroceries));
        assertEquals(1, spendingList.sizeOfList());
        assertEquals(previousSetSize, spendingList.sizeOfSet());
    }

    @Test
    void testRemoveCategory() {
        assertTrue(spendingList.removeCategory(recordGroceries.getCategory()));
        assertFalse(spendingList.getCategories().contains(recordGroceries.getCategory()));
    }

    @Test
    void testGetRecord() {
        assertEquals(spendingList.getRecords().get(0), spendingList.getRecord(0));
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
        List<Record> expectedList = spendingList.getRecords();
        spendingList.sortByDate();
        assertEquals(expectedList, spendingList.getRecords());
    }

    @Test
    void testSortByDateManuallyAddNewRecord() {
        Record manualRecord = null;
        try {
            manualRecord = new Record();
            manualRecord.setTitle("Jeans");
            manualRecord.setAmount(560);
            manualRecord.setCategory("Clothes");
            spendingList.addRecord(manualRecord);
        } catch (NegativeAmountException e) {
            fail("Amount is actually acceptable");
            e.printStackTrace();
        } catch (NameException e) {
            fail("Title and category are actually acceptable");
            e.printStackTrace();
        }
        List<Record> expectedList = Arrays.asList(manualRecord, recordGroceries, recordTravel);
        spendingList.sortByDate();
        assertEquals(expectedList, spendingList.getRecords());
    }

    @Test
    void testSortByAmountSpent() {
        spendingList.addRecord(notAddedRecord);
        List<Record> expectedList = Arrays.asList(notAddedRecord, recordTravel, recordGroceries);
        spendingList.sortByAmountSpent();
        assertEquals(expectedList, spendingList.getRecords());
    }

    @Test
    void testSortByAmountSpentSameAmount() {
        try {
            notAddedRecord.setAmount(recordGroceries.getAmount());
            spendingList.addRecord(notAddedRecord);
        } catch (NegativeAmountException e) {
            fail("Amount is actually acceptable");
        }
        spendingList.sortByAmountSpent();
        List<Record> expectedList = Arrays.asList(recordTravel, notAddedRecord, recordGroceries);
        assertEquals(expectedList, spendingList.getRecords());
    }

    @Test
    void testSortByCategory() {
        spendingList.sortByCategory();
        List<Record> expectedList = Arrays.asList(recordGroceries, recordTravel);
        assertEquals(expectedList, spendingList.getRecords());
    }

    @Test
    void testSortByCategorySameCategory() {
        try {
            notAddedRecord.setCategory(TRAVEL_CATEGORY);
            spendingList.addRecord(notAddedRecord);
        } catch (NameException e) {
            fail("Category name is actually acceptable");
        }
        spendingList.sortByCategory();
        List<Record> expectedList = Arrays.asList(recordGroceries, notAddedRecord, recordTravel);
        assertEquals(expectedList, spendingList.getRecords());
    }

    @Test
    void testEqualsReference() {
        SpendingList list = spendingList;
        assertEquals(spendingList, list);
    }

    @Test
    void testEqualsNullOrDiffObject() {
        assertNotEquals(spendingList, null);
        assertNotEquals(spendingList, new ArrayList<String>());
    }

    @Test
    void testEqualsDiffRecordsList() {
        try {
            Record record = new Record(recordTravel.getTitle() + "...",
                    recordTravel.getAmount(), recordTravel.getCategory());
            SpendingList list = new SpendingList();
            list.getRecords().addAll(spendingList.getRecords());

            list.addRecord(record);
            list.getCategories().addAll(spendingList.getCategories());
            assertNotEquals(spendingList, list);
        } catch (NameException e) {
            fail("Title is actually valid");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Amount is actually valid");
            e.printStackTrace();
        }
    }

    @Test
    void testEqualsSameList() {
        SpendingList list = new SpendingList();
        list.getRecords().addAll(spendingList.getRecords());
        list.getCategories().addAll(spendingList.getCategories());
        assertEquals(spendingList, list);
    }

    @Test
    void testHashCode() {
        SpendingList list = new SpendingList();
        list.getRecords().addAll(spendingList.getRecords());
        list.getCategories().addAll(spendingList.getCategories());
        assertEquals(spendingList.hashCode(), list.hashCode());
    }

    @Test
    void testToString() {
        String expected = SpendingList.class.getSimpleName() +
                "[spendingList=" + spendingList.getRecords() + "]";
        assertEquals(expected, spendingList.toString());
    }

    // EFFECTS: inits test records
    private void initRecords() {
        try {
            recordTravel = new Record("Went to Toronto", 401.34, TRAVEL_CATEGORY);
            Thread.sleep(10);
            recordGroceries = new Record("Went to SaveOnFoods", 100.76, GROCERIES_CATEGORY);
            Thread.sleep(10);
            notAddedRecord = new Record("Other record",
                    Math.max(recordTravel.getAmount(), recordGroceries.getAmount()) * 10,
                    "Ze New Category");
        } catch (NameException | NegativeAmountException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}