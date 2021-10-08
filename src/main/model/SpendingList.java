package model;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpendingList {

    private LinkedList<Entry> spendingList;

    // EFFECTS: creates empty spending list
    public SpendingList() {
        spendingList = new LinkedList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a new entry to the front of spending list
    public void addEntry(Entry entry) {
        spendingList.addFirst(entry);
    }

    // MODIFIES: this
    // EFFECTS: removes entry by its id from spending list and returns true,
    //          false otherwise
    public boolean removeById(int id) {
        return spendingList.stream()
                .filter(e -> e.getId() == id)
                .findAny()
                .map(spendingList::remove)
                .orElse(false);
    }

    // EFFECTS: if entry with provided id exists, returns this entry
    //          throws IllegalArgumentException otherwise
    public Entry findById(int id) {
        return spendingList.stream()
                .filter(e -> e.getId() == id)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    // EFFECTS: returns true if entry with provided id exists
    //          returns false otherwise
    public boolean isValidId(int id) {
        return spendingList.stream()
                .filter(e -> e.getId() == id)
                .findAny()
                .map(spendingList::contains)
                .orElse(false);
    }

    public List<Entry> getSpendingList() {
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
    // effects: sorts spending list by amount spent in descending order
    public void sortByAmountSpent() {
        sort(Comparator.comparing(Entry::getAmount).reversed());
    }

    // MODIFIES: this
    // effects: sorts spending list by date added in descending order (from newer to older)
    public void sortByDate() {
        sort(Comparator.comparing(Entry::getTimeAdded).reversed());
    }

    // MODIFIES: this
    // effects: sorts spending list by category title in alphabetical order
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
}