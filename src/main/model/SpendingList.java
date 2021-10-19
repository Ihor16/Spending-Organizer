package model;

import org.json.JSONArray;
import persistence.WritableArray;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

// Represents the list of spending entries
public class SpendingList implements WritableArray {

    private LinkedList<Entry> spendingList;

    // EFFECTS: creates empty spending list
    public SpendingList() {
        spendingList = new LinkedList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a new entry to the front of the spending list
    public void addEntry(Entry entry) {
        spendingList.addFirst(entry);
    }

    // MODIFIES: this
    // EFFECTS: removes provided entry from spending list and returns true if the entry is removed,
    // INVARIANT: provided entry exists in the list
    public boolean remove(Entry entry) {
        return spendingList.remove(entry);
    }

    // EFFECTS: from the spending list returns entry at the given index
    // INVARIANT: there is an Entry at the given index
    public Entry getEntry(int index) {
        return spendingList.get(index);
    }

    public LinkedList<Entry> getSpendingList() {
        return spendingList;
    }

    // EFFECTS: returns the length of spending list
    public int length() {
        return spendingList.size();
    }

    // EFFECTS: returns true if the spending list is empty,
    //          false otherwise
    public boolean isEmpty() {
        return spendingList.isEmpty();
    }

    // MODIFIES: this
    // EFFECTS: sorts spending list by amount spent in descending order (from most expensive to least expensive)
    public void sortByAmountSpent() {
        sort(Comparator.comparing(Entry::getAmount).reversed());
    }

    // MODIFIES: this
    // EFFECTS: sorts spending list by date added in descending order (from newer to older)
    public void sortByDate() {
        sort(Comparator.comparing(Entry::getTimeAdded).reversed());
    }

    // MODIFIES: this
    // EFFECTS: sorts spending list by category title in alphabetical order
    public void sortByCategory() {
        sort(Comparator.comparing(Entry::getCategory));
    }

    // MODIFIES: this
    // EFFECTS: sorts spending list according to the comparator
    private void sort(Comparator<Entry> comparator) {
        spendingList = spendingList.stream()
                .sorted(comparator)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public JSONArray toJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (Entry entry : spendingList) {
            jsonArray.put(entry.toJsonObject());
        }
        return jsonArray;
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
        return Objects.equals(spendingList, that.spendingList);
    }

    @Override
    public int hashCode() {
        return spendingList != null ? spendingList.hashCode() : 0;
    }
}