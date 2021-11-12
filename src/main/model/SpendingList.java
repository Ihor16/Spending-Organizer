package model;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.WritableObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// Class representing list of spending records
// INVARIANT: Categories is constructed first,
//            categories of records is a subset of categories in Categories,
//            filteredRecords is a subset of records
public class SpendingList implements WritableObject {

    private final ObservableList<Record> records;
    private final ObservableList<Record> filteredRecords;
    private final Categories categories;

    public SpendingList(@NotNull Categories categories) {
        this.categories = categories;
        this.records = FXCollections.observableArrayList();
        this.filteredRecords = FXCollections.observableArrayList();
    }

    // MODIFIES: this
    // EFFECTS: adds a new record to the front of the records list
    // INVARIANT: record is valid
    public void addRecord(@NotNull Record record) {
        records.add(0, record);
    }

    // MODIFIES: this
    // TODO: rewrite the filtered list documentation
    // EFFECTS: removes record from records list and returns true if the record is removed
    // INVARIANT: record exists in the list
    public boolean removeRecord(@NotNull Record record) {
        return records.remove(record);
    }


    // EFFECTS: returns list of dates of this.records,
    //          list is sorted by date (from more recent to less recent)
    public List<LocalDate> getDates() {
        return records.stream()
                .map(Record::getTimeAdded)
                .map(LocalDateTime::toLocalDate)
                .map(t -> t.withDayOfMonth(1))
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    // EFFECTS: filters records that occur in [from, to] time range, and
    //          returns a map of these records grouped by category, i.e.,
    //          {category: sum of amounts of all records that have this category};
    //          returned map is sorted by values (amounts)
    public Map<String, Double> groupByCategory(@NotNull LocalDate from, @NotNull LocalDate to) {
        List<Record> filteredRecords = filterRecordsByDate(from, to);
        Map<String, Double> map = filteredRecords.stream()
                .collect(Collectors.groupingBy(r -> r.getCategory().getName(),
                        Collectors.summingDouble(Record::getAmount)));
        return sortMapByValue(map);
    }

    // EFFECTS: returns a map of records grouped by category, i.e.,
    //          {category: list of records which have this category};
    private Map<String, List<Record>> groupByCategory(List<Record> filteredRecords) {
        return filteredRecords.stream()
                .collect(Collectors.groupingBy(r -> r.getCategory().getName()));
    }

    // EFFECTS: filters records that occur in [from, to] time range, and
    //          returns a map of these records grouped by category and date added, i.e.,
    //          {Category:
    //              {Date record was added (LocalDate that starts at first day of month):
    //                  sum of amounts of all records that have this category and were added in this month
    //               }}
    //          returned map is sorted by category (in alphabetical order) and by date (from older to newer)
    public Map<String, Map<LocalDate, Double>> groupByCategoryAndDate(@NotNull LocalDate from, @NotNull LocalDate to) {
        List<Record> filteredRecords = filterRecordsByDate(from, to);
        Map<String, List<Record>> groupedByCategories = groupByCategory(filteredRecords);
        Map<String, List<Record>> sortedGroupedByCategories = sortMapByKey(groupedByCategories);

        return groupByDate(sortedGroupedByCategories);
    }

    // EFFECTS: returns a new map that transforms unwrappedMap's List<Record> value to Map<LocalDate, Double>
    //          LocalDate - date record was created,
    //          Double - sum of amounts of records which were created in same month and have same category
    private Map<String, Map<LocalDate, Double>> groupByDate(Map<String, List<Record>> unwrappedMap) {
        Map<String, Map<LocalDate, Double>> result = new LinkedHashMap<>();
        unwrappedMap.forEach((key, value) -> {
            Map<LocalDate, Double> subMap = value.stream()
                    .collect(Collectors.groupingBy(r -> r.getTimeAdded().toLocalDate().withDayOfMonth(1),
                            Collectors.summingDouble(Record::getAmount)));
            result.put(key, sortMapByValue(subMap));
        });
        return result;
    }

    public ObservableList<Record> getRecords() {
        return records;
    }

    public Categories getCategories() {
        return categories;
    }

//    // TODO: implement filtering by isSelected in Record
//    public ObservableList<Record> getFilteredRecords() {
////        filteredRecords.clear();
////        filteredRecords.addAll(records);
////        records.filtered(r -> r.getCategory().equals("Groceries")).forEach(filteredRecords::add);
//        return filteredRecords;
//    }

    // EFFECTS: returns a new list of records that are created in time interval between from and to
    private List<Record> filterRecordsByDate(LocalDate from, LocalDate to) {
        return records.stream()
                .filter(fromToPredicate(from, to))
                .collect(Collectors.toList());
    }

    // EFFECTS: returns a predicate for filtering records that occur in [from, to] time interval
    private Predicate<Record> fromToPredicate(@NotNull LocalDate from, @NotNull LocalDate to) {
        return r -> r.getTimeAdded().isAfter(from.atStartOfDay())
                && r.getTimeAdded().isBefore(to.atTime(LocalTime.MAX));
    }

    // EFFECTS: returns a new map, which is a sorted by key
    private <K extends Comparable<? super K>, V> Map<K, V> sortMapByKey(Map<K, V> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    // EFFECTS: returns a new map, which is a sorted by value
    // Implementation is based on: https://mkyong.com/java/how-to-sort-a-map-in-java/
    private <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    // EFFECTS: returns this as Json Object
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("records", recordsToJsonArray());
        json.put("categories", categories.toJsonArray());
        return json;
    }

    // EFFECTS: returns records from this list as Json Array
    private JSONArray recordsToJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (Record record : records) {
            jsonArray.put(record.toJsonObject());
        }
        return jsonArray;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpendingList.class.getSimpleName() + "[", "]")
                .add("records=" + records)
                .add("categories=" + categories.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SpendingList that = (SpendingList) o;

        if (!records.equals(that.records)) {
            return false;
        }
        return categories.getCategories().equals(that.categories.getCategories());
    }

    @Override
    public int hashCode() {
        int result = records.hashCode();
        result = 31 * result + categories.hashCode();
        return result;
    }
}