package model;

import model.exceptions.NonExistentIdException;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

// Represents the list of spending entries
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
    // EFFECTS: removes entry by its id from spending list,
    //          throws WrongIdException if entry with such id isn't found
    public boolean removeById(int id) throws NonExistentIdException {
        return spendingList.stream()
                .filter(e -> e.getId() == id)
                .findAny()
                .map(spendingList::remove)
                .orElseThrow(() -> new NonExistentIdException(id));
    }

    // EFFECTS: if entry with provided id exists, returns this entry,
    //          throws WrongIdException if entry with such id isn't found
    public Entry findById(int id) throws NonExistentIdException {
        return spendingList.stream()
                .filter(e -> e.getId() == id)
                .findAny()
                .orElseThrow(() -> new NonExistentIdException(id));
    }

//    // EFFECTS: returns true if entry with provided id exists,
//    //          returns false otherwise
//    public boolean isValidId(int id) {
//        return spendingList.stream()
//                .filter(e -> e.getId() == id)
//                .findAny()
//                .map(spendingList::contains)
//                .orElse(false);
//    }

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
    // EFFECTS: sorts spending list by amount spent in descending order
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
}