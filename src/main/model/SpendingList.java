package model;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.WritableObject;

import java.util.StringJoiner;

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

    public ObservableList<Record> getRecords() {
        return records;
    }

    public Categories getCategories() {
        return categories;
    }

    // TODO: implement filtering by isSelected in Record
    public ObservableList<Record> getFilteredRecords() {
//        filteredRecords.clear();
//        filteredRecords.addAll(records);
//        records.filtered(r -> r.getCategory().equals("Groceries")).forEach(filteredRecords::add);
        return filteredRecords;
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