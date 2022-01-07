package model;

import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SpendingListGroupingTest {

    private SpendingList spendingList;
    private final LocalDate dateAdded = LocalDate.of(2021, Month.APRIL, 15);
    private final LocalDate laterDateAdded = LocalDate.of(2021, Month.NOVEMBER, 23);
    Categories categories;

    @BeforeEach
    void setUp() {
        try {
            categories = new Categories();
        } catch (NameException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetMonthsEmptySpendingList() {
        try {
            initEmptySpendingList();
        } catch (NameException e) {
            fail("Default category is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(Collections.emptyList(), spendingList.getDates());
    }

    @Test
    void testGetMonthsDiffMonths() {
        initFullSpendingList();
        LocalDateTime newTimeAdded = LocalDateTime.of(laterDateAdded, LocalTime.now());
        getByIndex(spendingList.getRecords().size() - 1).setTimeAdded(newTimeAdded.toString());

        List<LocalDate> expectedList = Arrays.asList(
                newTimeAdded.toLocalDate().withDayOfMonth(1),
                dateAdded.withDayOfMonth(1));

        assertEquals(expectedList, spendingList.getDates());
    }

    @Test
    void testGetMonthsDiffYears() {
        initFullSpendingList();
        LocalDateTime firstTimeAdded = LocalDateTime.of(
                LocalDate.of(2020, Month.NOVEMBER, 23),
                LocalTime.now());
        LocalDateTime lastTimeAdded = LocalDateTime.of(
                LocalDate.of(2022, Month.NOVEMBER, 23),
                LocalTime.now());

        getByIndex(0).setTimeAdded(firstTimeAdded.toString());
        getByIndex(spendingList.getRecords().size() - 1).setTimeAdded(lastTimeAdded.toString());

        List<LocalDate> expectedList = Arrays.asList(
                lastTimeAdded.toLocalDate().withDayOfMonth(1),
                dateAdded.withDayOfMonth(1),
                firstTimeAdded.toLocalDate().withDayOfMonth(1));

        assertEquals(expectedList, spendingList.getDates());
    }

    @Test
    void testGetMonthsSameMonths() {
        initFullSpendingList();
        assertEquals(Collections.singletonList(dateAdded.withDayOfMonth(1)), spendingList.getDates());
    }

    @Test
    void testGroupByCategoryOneMonthAllRecords() {
        initFullSpendingList();
        Map<String, Double> expectedMap = new LinkedHashMap<>();
        spendingList.getRecords().forEach(r -> expectedMap.put(r.getCategory().getName(), r.getAmount()));
        assertEquals(expectedMap, spendingList.groupByCategory(LocalDate.MIN));
    }

    @Test
    void testGroupByCategoryOneMonth() {
        initFullSpendingList();
        Map<String, Double> expectedMap = new LinkedHashMap<>();
        spendingList.getRecords().forEach(r -> expectedMap.put(r.getCategory().getName(), r.getAmount()));
        assertEquals(expectedMap, spendingList.groupByCategory(dateAdded.withDayOfMonth(1)));
    }

    @Test
    void testGroupByCategoryWrongDateOrder() {
        initFullSpendingList();
        assertEquals(Collections.emptyMap(), spendingList.groupByCategory(laterDateAdded, dateAdded));
    }

    @Test
    void testGroupByCategoryDateBound() {
        spendingList = new SpendingList(categories);
        try {
            Record r1 = new Record("Title 1", 200, new Category("Category 1", categories));
            r1.setTimeAdded(returnDate(2021, Month.APRIL, 1, 0, 0, 1));

            Record r2 = new Record("Title 1", 200, new Category("Category 2", categories));
            r2.setTimeAdded(returnDate(2021, Month.APRIL, Month.APRIL.length(Year.isLeap(2021)),
                    23, 59, 59));

            Record r3 = new Record("Title 2", 300, new Category("Category 3", categories));
            r3.setTimeAdded(returnDate(2021, Month.MAY, 1, 0, 0, 1));

            Arrays.asList(r1, r2).forEach(spendingList::addRecord);
        } catch (NameException | NegativeAmountException e) {
            fail("Not the case: " + e.getMessage());
        }
        LocalDate month = LocalDate.of(2021, Month.APRIL, 1);
        int returnedEntriesSize = spendingList.groupByCategory(month).entrySet().size();
        assertEquals(2, returnedEntriesSize);
    }

    @Test
    void testGroupByCategorySameMonthSameCategory() {
        initFullSpendingList();
        String categoryName = "Same category";
        try {
            for (Record r : spendingList.getRecords()) {
                r.setCategory(new Category(categoryName, categories));
            }
        } catch (NameException e) {
            fail("Not the case: " + e.getMessage());
        }

        double expectedSum = IntStream.rangeClosed(1, spendingList.getRecords().size()).sum() * 100;
        Map<String, Double> expectedMap = new HashMap<>();
        expectedMap.put(categoryName, expectedSum);
        assertEquals(expectedMap, spendingList.groupByCategory(dateAdded, dateAdded));
    }

    @Test
    void testGroupByCategorySameMonthDiffCategories() {
        spendingList = new SpendingList(categories);

        try {
            Record r1 = new Record("Title 1", 200, new Category("Category 1", categories));
            Record r2 = new Record("Title 2", 300, new Category("Category 2", categories));
            Record r3 = new Record("Title 3", 400, new Category("Category 2", categories));
            Record r4 = new Record("Title 4", 500, new Category("Category 4", categories));
            Arrays.asList(r1, r2, r3, r4).forEach(spendingList::addRecord);
        } catch (NameException | NegativeAmountException e) {
            fail("Not the case: " + e.getMessage());
        }

        Map<String, Double> expectedMap = new LinkedHashMap<>();
        expectedMap.put("Category 2", 300 + 400.0);
        expectedMap.put("Category 4", 500.0);
        expectedMap.put("Category 1", 200.0);

        assertEquals(expectedMap, spendingList.groupByCategory(LocalDate.now(), LocalDate.now()));
    }

    @Test
    void testGroupByCategoryDiffMonthSameCategory() {
        initFullSpendingList();
        getByIndex(0).setTimeAdded(LocalDateTime.of(laterDateAdded, LocalTime.now()).toString());

        Map<String, Double> expectedMap = new LinkedHashMap<>();
        spendingList.getRecords().forEach(r -> expectedMap.put(r.getCategory().getName(), r.getAmount()));

        assertEquals(expectedMap, spendingList.groupByCategory(dateAdded, laterDateAdded));
    }

    @Test
    void testGroupByCategoryDiffMonthSameCategoryOutOfDate() {
        initFullSpendingList();
        getByIndex(0).setTimeAdded(LocalDateTime.of(laterDateAdded, LocalTime.now()).toString());

        Map<String, Double> expectedMap = new LinkedHashMap<>();
        spendingList.getRecords().forEach(r -> expectedMap.put(r.getCategory().getName(), r.getAmount()));
        expectedMap.remove(getByIndex(0).getCategory().getName());

        assertEquals(expectedMap, spendingList.groupByCategory(dateAdded, dateAdded));
    }

    @Test
    void testGroupByCategoryDiffMonthDiffCategories() {
        spendingList = new SpendingList(categories);

        try {
            Record r1 = new Record("Title 1", 200, new Category("Category 1", categories));
            r1.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r2 = new Record("Title 2", 300, new Category("Category 2", categories));
            r2.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r3 = new Record("Title 3", 400, new Category("Category 2", categories));
            r3.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r4 = new Record("Title 4", 500, new Category("Category 4", categories));
            r4.setTimeAdded(LocalDateTime.of(laterDateAdded, LocalTime.now()).toString());
            Arrays.asList(r1, r2, r3, r4).forEach(spendingList::addRecord);
        } catch (NameException | NegativeAmountException e) {
            fail("Not the case: " + e.getMessage());
        }

        Map<String, Double> expectedMap = new LinkedHashMap<>();
        expectedMap.put("Category 2", 400 + 300.0);
        expectedMap.put("Category 4", 500.0);
        expectedMap.put("Category 1", 200.0);

        assertEquals(expectedMap, spendingList.groupByCategory(dateAdded, laterDateAdded));
    }

    @Test
    void testGroupByCategoryAndDateEmptySpendingList() {
        try {
            initEmptySpendingList();
        } catch (NameException e) {
            fail("Default category is actually acceptable");
            e.printStackTrace();
        }
        assertEquals(Collections.emptyMap(), spendingList.groupByCategoryAndDate(dateAdded, dateAdded));
    }

    @Test
    void testGroupByCategoryAndDateWrongDateOrder() {
        initFullSpendingList();
        assertEquals(Collections.emptyMap(), spendingList.groupByCategoryAndDate(laterDateAdded, dateAdded));
    }

    @Test
    void testGroupByCategoryAndDateOneMonth() {
        initFullSpendingList();
        Map<String, Map<LocalDate, Double>> expectedMap = new LinkedHashMap<String, Map<LocalDate, Double>>() {{
            spendingList.getRecords().forEach(r -> {
                put(r.getCategory().getName(), new LinkedHashMap<LocalDate, Double>() {{
                    put(r.getTimeAdded().toLocalDate().withDayOfMonth(1), r.getAmount());
                }});
            });
        }};
        assertEquals(expectedMap, spendingList.groupByCategoryAndDate(dateAdded.withDayOfMonth(1)));
    }

    @Test
    void testGroupByCategoryAndDateMinDate() {
        initFullSpendingList();
        Map<String, Map<LocalDate, Double>> expectedMap = new LinkedHashMap<String, Map<LocalDate, Double>>() {{
            spendingList.getRecords().forEach(r -> {
                put(r.getCategory().getName(), new LinkedHashMap<LocalDate, Double>() {{
                    put(r.getTimeAdded().toLocalDate().withDayOfMonth(1), r.getAmount());
                }});
            });
        }};
        assertEquals(expectedMap, spendingList.groupByCategoryAndDate(LocalDate.MIN));
    }

    @Test
    void testGroupByCategoryAndDateSameCategorySameMonth() {
        spendingList = new SpendingList(categories);

        try {
            Record r1 = new Record("Title 1", 200, new Category("Category 1", categories));
            Record r2 = new Record("Title 2", 300, new Category("Category 1", categories));
            Record r3 = new Record("Title 3", 400, new Category("Category 1", categories));
            Record r4 = new Record("Title 4", 500, new Category("Category 1", categories));
            Arrays.asList(r1, r2, r3, r4).forEach(spendingList::addRecord);
        } catch (NameException | NegativeAmountException e) {
            e.printStackTrace();
        }
        Map<String, Map<LocalDate, Double>> expectedMap = new LinkedHashMap<String, Map<LocalDate, Double>>()
        {{
            put("Category 1", new LinkedHashMap<LocalDate, Double>() {{
                put(LocalDate.now().withDayOfMonth(1), 200.0 + 300 + 400 + 500);
            }});
        }};

        assertEquals(expectedMap, spendingList.groupByCategoryAndDate(LocalDate.now(), LocalDate.now()));
    }

    @Test
    void testGroupByCategoryAndDateDiffCategorySameMonth() {
        spendingList = new SpendingList(categories);

        try {
            Record r1 = new Record("Title 1", 200, new Category("Category 1", categories));
            Record r2 = new Record("Title 2", 300, new Category("Category 2", categories));
            Record r3 = new Record("Title 3", 400, new Category("Category 2", categories));
            Record r4 = new Record("Title 4", 500, new Category("Category 4", categories));
            Arrays.asList(r1, r2, r3, r4).forEach(spendingList::addRecord);
        } catch (NameException | NegativeAmountException e) {
            e.printStackTrace();
        }
        Map<String, Map<LocalDate, Double>> expectedMap = new LinkedHashMap<String, Map<LocalDate, Double>>()
        {{
            put("Category 1", new LinkedHashMap<LocalDate, Double>() {{
                put(LocalDate.now().withDayOfMonth(1), 200.0);
            }});
            put("Category 2", new LinkedHashMap<LocalDate, Double>() {{
                put(LocalDate.now().withDayOfMonth(1), 300.0 + 400);
            }});
            put("Category 4", new LinkedHashMap<LocalDate, Double>() {{
                put(LocalDate.now().withDayOfMonth(1), 500.0);
            }});
        }};

        assertEquals(expectedMap, spendingList.groupByCategoryAndDate(LocalDate.now(), LocalDate.now()));
    }

    @Test
    void testGroupByCategoryAndDateSameCategoryDiffMonth() {
        spendingList = new SpendingList(categories);

        try {
            Record r1 = new Record("Title 1", 200, new Category("Category 1", categories));
            r1.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r2 = new Record("Title 2", 300, new Category("Category 1", categories));
            r2.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r3 = new Record("Title 3", 400, new Category("Category 1", categories));
            r3.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r4 = new Record("Title 4", 500, new Category("Category 1", categories));
            r4.setTimeAdded(LocalDateTime.of(laterDateAdded, LocalTime.now()).toString());
            Arrays.asList(r1, r2, r3, r4).forEach(spendingList::addRecord);
        } catch (NameException | NegativeAmountException e) {
            e.printStackTrace();
        }
        Map<String, Map<LocalDate, Double>> expectedMap = new LinkedHashMap<String, Map<LocalDate, Double>>()
        {{
            put("Category 1", new LinkedHashMap<LocalDate, Double>() {{
                put(laterDateAdded.withDayOfMonth(1), 500.0);
                put(dateAdded.withDayOfMonth(1), 200.0 + 300 + 400);
            }});
        }};

        assertEquals(expectedMap, spendingList.groupByCategoryAndDate(dateAdded, laterDateAdded));
    }

    @Test
    void testGroupByCategoryAndDateDiffCategoryDiffMonth() {
        spendingList = new SpendingList(categories);

        try {
            Record r1 = new Record("Title 1", 200, new Category("Category 1", categories));
            r1.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r2 = new Record("Title 2", 300, new Category("Category 2", categories));
            r2.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r3 = new Record("Title 3", 400, new Category("Category 3", categories));
            r3.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Record r4 = new Record("Title 4", 500, new Category("Category 2", categories));
            r4.setTimeAdded(LocalDateTime.of(laterDateAdded, LocalTime.now()).toString());
            Record r5 = new Record("Title 5", 500, new Category("Category 2", categories));
            r5.setTimeAdded(LocalDateTime.of(laterDateAdded, LocalTime.now()).toString());
            Record r6 = new Record("Title 6", 0, new Category("Category 4", categories));
            r6.setTimeAdded(LocalDateTime.of(laterDateAdded, LocalTime.now()).toString());
            Record r7 = new Record("Title 7", 100, new Category("Category 4", categories));
            r7.setTimeAdded(LocalDateTime.of(dateAdded, LocalTime.now()).toString());
            Arrays.asList(r1, r2, r3, r4, r5, r6, r7).forEach(spendingList::addRecord);
        } catch (NameException | NegativeAmountException e) {
            e.printStackTrace();
        }
        Map<String, Map<LocalDate, Double>> expectedMap = new LinkedHashMap<String, Map<LocalDate, Double>>()
        {{
            put("Category 1", new LinkedHashMap<LocalDate, Double>() {{
                put(dateAdded.withDayOfMonth(1), 200.0);
            }});
            put("Category 2", new LinkedHashMap<LocalDate, Double>() {{
                put(dateAdded.withDayOfMonth(1), 300.0);
                put(laterDateAdded.withDayOfMonth(1), 500.0 + 500);
            }});
            put("Category 3", new LinkedHashMap<LocalDate, Double>() {{
                put(dateAdded.withDayOfMonth(1), 400.0);
            }});
            put("Category 4", new LinkedHashMap<LocalDate, Double>() {{
                put(dateAdded.withDayOfMonth(1), 100.0);
                put(laterDateAdded.withDayOfMonth(1), 0.0);
            }});
        }};

        assertEquals(expectedMap, spendingList.groupByCategoryAndDate(dateAdded, laterDateAdded));
    }

    private String returnDate(int year, Month month, int day, int hour, int minute, int second) {
        return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(hour, minute, second)).toString();
    }

    private Record getByIndex(int index) {
        return spendingList.getRecords().get(index);
    }

    private void initEmptySpendingList() throws NameException {
        categories = new Categories();
        spendingList = new SpendingList(categories);
    }

    // Effects: generates spending list with records, e.g.,
    //          [Record "Title 1", 100, "Category 1"],
    //          [Record "Title 2", 200, "Category 2"],
    //          [Record "Title 3", 300, "Category 3"]
    private void initFullSpendingList() {
        spendingList = new SpendingList(categories);

        try {
            for (int i = 1; i <= 7; i++) {
                Record r = new Record("Title " + i, i * 100,
                        new Category("Category " + i, categories));
                LocalDateTime timeAdded = LocalDateTime.of(dateAdded, LocalTime.now());
                r.setTimeAdded(timeAdded.toString());
                spendingList.addRecord(r);
                Thread.sleep(10);
            }
        } catch (NameException | NegativeAmountException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
