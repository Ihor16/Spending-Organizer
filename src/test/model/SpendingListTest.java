package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SpendingListTest {

    private Category groceriesCategory;
    private Category travelCategory;
    private Category notAddedCategory;
    private Categories categories;

    private SpendingList spendingList;

    private Record recordTravel;
    private Record recordGroceries;
    private Record notAddedRecord;

    @BeforeEach
    void setUp() {
        categories = new Categories();
        spendingList = new SpendingList(categories);
        initCategories();
        initRecords();
        spendingList.addRecord(recordTravel);
        spendingList.addRecord(recordGroceries);
    }

    @Test
    void testConstructor() {
        assertTrue(new SpendingList(categories).getRecords().isEmpty());
    }

    @Test
    void testAddRecord() {
        int oldSize = spendingList.getRecords().size();
        Categories oldCategories = categories;

        notAddedRecord.setCategory(travelCategory);
        spendingList.addRecord(notAddedRecord);

        assertEquals(oldSize + 1, spendingList.getRecords().size());
        assertEquals(notAddedRecord, spendingList.getRecords().get(0));
        assertTrue(categories.getCategories().contains(notAddedCategory));
        // Since we add existing category, set of categories doesn't change
        assertEquals(oldCategories, categories);
    }

    @Test
    void testRemoveRecord() {
        int oldSize = spendingList.getRecords().size();
        Categories oldCategories = categories;

        assertTrue(spendingList.removeRecord(recordGroceries));
        assertEquals(oldSize - 1, spendingList.getRecords().size());
        // Since we remove a record, set of categories doesn't change
        assertEquals(oldCategories, categories);
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
    void testEqualsDiffRecords() {
        SpendingList list = new SpendingList(categories);
        try {
            Record record = new Record(recordTravel.getTitle() + "...", recordTravel.getAmount(),
                    recordTravel.getCategory());

            list.getRecords().addAll(spendingList.getRecords());
            list.addRecord(record);
        } catch (NameException e) {
            fail("Title is actually valid");
            e.printStackTrace();
        } catch (NegativeAmountException e) {
            fail("Amount is actually valid");
            e.printStackTrace();
        }

        assertNotEquals(spendingList, list);
    }

    @Test
    void testEqualsSameList() {
        SpendingList list = new SpendingList(categories);
        list.getRecords().addAll(spendingList.getRecords());
        assertEquals(spendingList, list);
    }

    @Test
    void testHashCode() {
        SpendingList list = new SpendingList(categories);
        list.getRecords().addAll(spendingList.getRecords());
        assertEquals(spendingList.hashCode(), list.hashCode());
    }

    @Test
    void testToString() {
        SpendingList spendingList = new SpendingList(new Categories());
        spendingList.addRecord(recordTravel);
        String expected;
        expected = SpendingList.class.getSimpleName() + "[" +
                "records=[" +
                "Record[title=" + recordTravel.getTitle() + ", amount=" + recordTravel.getAmount() + ", category=" +
                "Category[name=" + travelCategory.getName() + "], timeAdded=" + recordTravel.getTimeAdded() + "]], " +
                "categories=" + Categories.class.getSimpleName() + "[categories=[" +
                "Category[name=default]]]]";
        assertEquals(expected, spendingList.toString());
    }

    // EFFECTS: inits test records
    private void initRecords() {
        try {
            recordTravel = new Record("Went to Toronto", 401.34, travelCategory);
            Thread.sleep(10);
            recordGroceries = new Record("Went to SaveOnFoods", 100.76, groceriesCategory);
            Thread.sleep(10);
            notAddedRecord = new Record("Other record", recordTravel.getAmount() * 195, notAddedCategory);
        } catch (NameException | NegativeAmountException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // EFFECTS: inits test categories
    private void initCategories() {
        try {
            groceriesCategory = new Category("Groceries", categories);
            travelCategory = new Category("Travel", categories);
            notAddedCategory = new Category("Not added category ...", categories);
        } catch (NameException e) {
            e.printStackTrace();
        }
    }
}