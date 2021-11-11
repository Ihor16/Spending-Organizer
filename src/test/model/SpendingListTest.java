package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

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

    private LocalDateTime travelDate = LocalDateTime.of(
            LocalDate.of(2020, Month.DECEMBER, 15), LocalTime.NOON);
    private LocalDateTime groceriesDate = LocalDateTime.of(
            LocalDate.of(2021, Month.APRIL, 21), LocalTime.of(17, 15));

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
    void testAddRecordWithExistingCategory() {
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

    @Test
    void testGroupByCategoryInSelectedDatesWrongOrder() {
        assertEquals(Collections.emptyMap(),
                spendingList.groupByCategoryInSelectedDates(groceriesDate.toLocalDate(),
                        travelDate.toLocalDate()));
    }

    @Test
    void testGroupByCategoryInSelectedDatesSameCategory() {
        recordTravel.setCategory(groceriesCategory);
        Map<String, Double> map = new HashMap<>();
        map.put(groceriesCategory.getName(), recordTravel.getAmount() + recordGroceries.getAmount());
        assertEquals(map, spendingList.groupByCategoryInSelectedDates(travelDate.toLocalDate(),
                groceriesDate.toLocalDate()));
    }

    @Test
    void testGroupByCategoryInSelectedDates() {
        Map<String, Double> map = new HashMap<>();
        recordTravel.setTimeAdded(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 15, 10))
                .toString());
        recordGroceries.setTimeAdded(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 15, 20))
                .toString());
        map.put(travelCategory.getName(), recordTravel.getAmount());
        map.put(groceriesCategory.getName(), recordGroceries.getAmount());
        assertEquals(map, spendingList.groupByCategoryInSelectedDates(recordTravel.getTimeAdded().toLocalDate(),
                recordTravel.getTimeAdded().toLocalDate()));
    }

    @Test
    void testGroupByMonthsInSelectedDates() {
//        fail();
    }

    @Test
    void testGroupByWeeksInSelectedDates() {
//        fail();
    }

    @Test
    void testGroupByDaysInSelectedDates() {
//        fail();
    }

    // EFFECTS: inits test records
    private void initRecords() {
        try {
            recordTravel = new Record("Went to Toronto", 401.34, travelCategory);
            recordTravel.setTimeAdded(travelDate.toString());
            Thread.sleep(10);
            recordGroceries = new Record("Went to SaveOnFoods", 100.76, groceriesCategory);
            recordGroceries.setTimeAdded(groceriesDate.toString());
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