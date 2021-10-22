package model;

import model.exceptions.NameException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.*;
import java.util.stream.Collectors;

// Represents the list of spending entries
public class SpendingList implements Writable {

    private LinkedList<Entry> spendingList;
    private SortedSet<String> categories;

    // EFFECTS: creates empty spending list and empty categories set
    public SpendingList() {
        spendingList = new LinkedList<>();
        categories = new TreeSet<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a new entry to the front of the spending list,
    //          adds this entry's category to the categories set
    // INVARIANT: entry is valid
    public void addEntry(Entry entry) {
        spendingList.addFirst(entry);
        // since categories is a set, it won't contain duplicates
        categories.add(entry.getCategory());
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

    // REQUIRES: entry exists in the list
    // MODIFIES: this
    // EFFECTS: removes entry from spending list and returns true if the entry is removed
    public boolean removeEntry(Entry entry) {
        return spendingList.remove(entry);
    }

    // REQUIRES: category exists in the list
    // MODIFIES: this
    // EFFECTS: removes category from the set and returns true if the category is removed
    public boolean removeCategory(String category) {
        return categories.remove(category);
    }

    // EFFECTS: returns entry from the spending list at the given index,
    //          throws IndexOutOfBoundsException if index is out of the range
    public Entry getEntry(int index) throws IndexOutOfBoundsException {
        return spendingList.get(index);
    }

    public Set<String> getCategories() {
        return categories;
    }

    public List<Entry> getSpendingList() {
        return spendingList;
    }

    // EFFECTS: returns the size of spending list
    public int sizeOfList() {
        return spendingList.size();
    }

    // EFFECTS: returns the size of categories set
    public int sizeOfSet() {
        return categories.size();
    }

    // EFFECTS: returns true if the spending list is empty,
    //          false otherwise
    public boolean isEmptyList() {
        return spendingList.isEmpty();
    }

    // MODIFIES: this
    // EFFECTS: sorts spending list by amount spent in descending order (from most expensive to least expensive)
    public void sortByAmountSpent() {
        sort(Comparator.comparing(Entry::getAmount).reversed());
    }

    // MODIFIES: this
    // EFFECTS: sorts spending list by date added (from newer to older)
    public void sortByDate() {
        sort(Comparator.comparing(Entry::getTimeAdded).reversed());
    }

    // MODIFIES: this
    // EFFECTS: sorts spending list by category in alphabetical order
    public void sortByCategory() {
        sort(Comparator.comparing(Entry::getCategory));
    }

    // MODIFIES: this
    // EFFECTS: sorts spending list according to comparator
    private void sort(Comparator<Entry> comparator) {
        spendingList = spendingList.stream()
                .sorted(comparator)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("categories", categoriesToJsonArray());
        json.put("records", entriesToJsonArray());
        return json;
    }

    // EFFECTS: returns entries from this list as Json Array
    private JSONArray entriesToJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (Entry entry : spendingList) {
            jsonArray.put(entry.toJsonObject());
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
        return Objects.equals(spendingList, that.spendingList);
    }

    @Override
    public int hashCode() {
        return spendingList != null ? spendingList.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpendingList.class.getSimpleName() + "[", "]")
                .add("spendingList=" + spendingList)
                .toString();
    }
}