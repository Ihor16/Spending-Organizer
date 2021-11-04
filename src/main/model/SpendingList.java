package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.exceptions.NameException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.*;
import java.util.stream.Collectors;

// Class representing list of records and categories
public class SpendingList implements Writable {

    private ObservableList<Record> records;
    private final Set<String> categories;

    // EFFECTS: creates empty records list and empty categories set
    public SpendingList() {
        records = FXCollections.observableArrayList();
        categories = new TreeSet<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a new record to the front of the records list, and sets this record's index
    //          adds this record's category to the categories set
    // INVARIANT: record is valid
    public void addRecord(Record record) {
        records.add(0, record);
        // since categories is a set, it won't contain duplicates
        categories.add(record.getCategory());
    }

    // MODIFIES: this
    // EFFECTS: trims category and adds it to the set,
    //          throws NameException if category is blank
    public void addCategory(String category) throws NameException {
        if (isBlank(category)) {
            throw new NameException("category");
        }
        categories.add(category.trim());
    }

    // REQUIRES: record exists in the list
    // MODIFIES: this
    // EFFECTS: removes record from records list and returns true if the record is removed
    public boolean removeRecord(Record record) {
        return records.remove(record);
    }

    // REQUIRES: category exists in the list
    // MODIFIES: this
    // EFFECTS: removes category from the set and returns true if the category is removed
    public boolean removeCategory(String category) {
        return categories.remove(category);
    }

    // EFFECTS: returns record from the records list at the given index,
    //          throws IndexOutOfBoundsException if index is out of the range
    public Record getRecord(int index) throws IndexOutOfBoundsException {
        return records.get(index);
    }

    public Set<String> getCategories() {
        return categories;
    }

    public ObservableList<Record> getRecords() {
        return records;
    }

    // EFFECTS: returns categories as observable list of type Map (required for GUI Table)
    // Based on: https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
    public ObservableList<Map<String, String>> getCategoriesAsObservableList(String key) {
        ObservableList<Map<String, String>> allData = FXCollections.observableArrayList();
        for (String c : categories) {
            Map<String, String> row = new HashMap<>();
            row.put(key, c);
            allData.add(row);
        }
        return allData;
    }

    // EFFECTS: returns the size of records list
    public int sizeOfList() {
        return records.size();
    }

    // EFFECTS: returns the size of categories set
    public int sizeOfSet() {
        return categories.size();
    }

    // EFFECTS: returns true if the records list is empty,
    //          false otherwise
    public boolean isEmptyList() {
        return records.isEmpty();
    }

    // MODIFIES: this
    // EFFECTS: sorts records list by amount spent in descending order (from most expensive to least expensive)
    public void sortByAmountSpent() {
        sort(Comparator.comparing(Record::getAmount).reversed());
    }

    // MODIFIES: this
    // EFFECTS: sorts records list by date added (from newer to older)
    public void sortByDate() {
        sort(Comparator.comparing(Record::getTimeAdded).reversed());
    }

    // MODIFIES: this
    // EFFECTS: sorts records list by category in alphabetical order
    public void sortByCategory() {
        sort(Comparator.comparing(Record::getCategory));
    }

    // MODIFIES: this
    // EFFECTS: sorts records list according to comparator
    private void sort(Comparator<Record> comparator) {
        records = records.stream()
                .sorted(comparator)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("categories", categoriesToJsonArray());
        json.put("records", recordsToJsonArray());
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

    // EFFECTS: returns categories as Json Array
    private JSONArray categoriesToJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (String c : categories) {
            jsonArray.put(c);
        }
        return jsonArray;
    }

    // EFFECTS: returns true if provided string is blank,
    //          false otherwise
    private boolean isBlank(String str) {
        // implementation of removing whitespaces is taken from
        // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        return str.replaceAll("[\\s]+", "").isEmpty();
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
        return categories.equals(that.categories);
    }

    @Override
    public int hashCode() {
        int result = records.hashCode();
        result = 31 * result + categories.hashCode();
        return result;
    }

    @Override
    // EFFECTS: returns a string of records list
    public String toString() {
        return new StringJoiner(", ", SpendingList.class.getSimpleName() + "[", "]")
                .add("spendingList=" + records)
                .toString();
    }
}