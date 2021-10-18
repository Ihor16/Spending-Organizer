package ui;

import model.Categories;
import model.Entry;
import model.SpendingList;
import model.exceptions.EntryFieldException;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import model.exceptions.NonExistentIdException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class SpendingApp {

    private static final String QUIT_COMMAND = "q";
    private Categories categories;
    private SpendingList spendingList;
    private Scanner input;

    // EFFECTS: inits categories, entries, scanner, and runs the app
    public SpendingApp() {
        try {
            initCategories();
            initEntries();
            initScanner();
            runApp();
        } catch (EntryFieldException e) {
            System.out.println("Couldn't initialize the required app components");
        }
    }

    // This method was designed based on the TellerApp UI class
    // MODIFIES: this
    // EFFECTS: runs the spending list application
    private void runApp() {
        String command;

        while (true) {
            displayMenu();
            command = input.next();
            if (command.equals(QUIT_COMMAND)) {
                System.out.println("\nHave a nice day!");
                break;
            } else {
                processCommand(command);
            }
        }
    }

    // EFFECTS: displays current list of entries and commands to operate on the list
    private void displayMenu() {
        printSpendingEntries();
        System.out.println("\nSelect one of these commands:");
        System.out.println("a -> add new entry");
        System.out.println("c -> change existing entry");
        System.out.println("r -> remove entry");
        System.out.println("s -> sort entries");
        System.out.println(QUIT_COMMAND + " -> quit");
    }

    // MODIFIES: this
    // EFFECTS: processes user's menu commands
    private void processCommand(String command) {
        switch (command) {
            case "a":
                addNewEntry(new Entry());
                break;
            case "r":
                removeEntry();
                break;
            case "s":
                sortEntries();
                break;
            case "c":
                changeEntry();
                break;
            default:
                enteredWrong("command");
                break;
        }
    }

    // MODIFIES: this and entry
    // EFFECTS: asks user to enter each entry field separately
    //          and populates entry fields if user input is valid
    private void addNewEntry(Entry entry) {
        String title = addTitle(entry);

        System.out.println("Title: " + title);
        double amount = addAmount(entry);

        System.out.println("Title: " + title);
        System.out.println("Amount: " + amount);
        String category = addCategory(entry);

        assert !(Objects.isNull(title) || Objects.isNull(category));
        spendingList.addEntry(entry);
    }

    // MODIFIES: entry
    // EFFECTS: asks user to enter category of financial entry and returns the entered category,
    //          asks user to enter again if they enter not acceptable category
    private String addCategory(Entry entry) {
        System.out.println("\nAdd Category: ");
        printCategories();
        String category = input.next();
        // This check won't happen in GUI because user will use drop-down list to select category
        if (categories.contains(category)) {
            entry.setCategory(category);
            return category;
        } else {
            return addCategory(entry);
        }
    }

    // MODIFIES: entry
    // EFFECTS: asks user to enter amount of financial entry and returns the entered amount,
    //          asks user to enter again if they enter not acceptable amount
    private double addAmount(Entry entry) {
        System.out.println("\nAdd Amount Spent in CAD");
        try {
            double amount = Double.parseDouble(input.next());
            entry.setAmount(amount);
            return amount;
        } catch (NegativeAmountException e) {
            System.out.println("Error: " + e.getMessage());
            return addAmount(entry);
        } catch (NumberFormatException e) {
            enteredWrong("number format");
            return addAmount(entry);
        }
    }

    // MODIFIES: entry
    // EFFECTS: asks user to enter title of financial entry and returns the entered title,
    //          asks user to enter again if they enter not acceptable title
    private String addTitle(Entry entry) {
        System.out.println("\nAdd Title: ");
        try {
            String title = input.next();
            entry.setTitle(title);
            return title;
        } catch (NameException e) {
            System.out.println("Error: " + e.getMessage());
            return addTitle(entry);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes entry by its id if an entry with such id is found,
    //          asks user to enter id again otherwise
    private void removeEntry() {
        try {
            int id = readEntryId();
            spendingList.removeById(id);
        } catch (NonExistentIdException e) {
            System.out.println("Error: " + e.getMessage());
            removeEntry();
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user how they want to sort the entries and sorts them,
    //          asks user to reenter sorting command if they entered a wrong one
    private void sortEntries() {
        printSortCommands();
        switch (input.next()) {
            case "am":
                spendingList.sortByAmountSpent();
                break;
            case "ca":
                spendingList.sortByCategory();
                break;
            case "da":
                spendingList.sortByDate();
                break;
            default:
                enteredWrong("sorting command");
                printSpendingEntries();
                sortEntries();
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user to input id of the entry they want to change and specify the change,
    //          asks them to reenter id, if entered id isn't found
    private void changeEntry() {
        int id = readEntryId();
        try {
            Entry foundEntry = spendingList.findById(id);
            System.out.println(foundEntry);
            printChangeCommands();
            String command = input.next();
            if (!command.equals(QUIT_COMMAND)) {
                processChange(foundEntry, command);
            }
        } catch (NonExistentIdException e) {
            System.out.println("Error: " + e.getMessage());
            printSpendingEntries();
            changeEntry();
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user if they want to further change the entry or quit the change menu,
    //          changes specified entry field if user wants to change it
    // INVARIANT: entry is valid
    private void changeEntry(Entry entry) {
        assert !(Objects.isNull(entry.getTitle()) || Objects.isNull(entry.getCategory()));
        System.out.println(entry);
        printChangeCommands();
        String command = input.next();
        if (!command.equals(QUIT_COMMAND)) {
            processChange(entry, command);
        }
    }

    // MODIFIES: entry
    // EFFECTS: changes specific aspects of the entry,
    //          and asks if user wants to change entry further
    private void processChange(Entry entry, String command) {
        switch (command) {
            case "ti":
                changeTitle(entry);
                break;
            case "am":
                changeAmount(entry);
                break;
            case "ca":
                changeCategory(entry);
                break;
            default:
                enteredWrong("changing command");
                break;
        }
        changeEntry(entry);
    }

    // MODIFIES: entry
    // EFFECTS: changes entry's title,
    //          asks user to reenter title if their input is unacceptable
    private void changeTitle(Entry entry) {
        System.out.println("\nNew title:");
        try {
            String title = input.next();
            entry.setTitle(title);
        } catch (NameException e) {
            System.out.println("Error: " + e.getMessage());
            changeTitle(entry);
        }
    }

    // MODIFIES: entry
    // EFFECTS: changes entry's amount
    //          asks user to reenter amount if their input is unacceptable
    private void changeAmount(Entry entry) {
        System.out.println("\nNew amount: CAD");
        try {
            double amount = Double.parseDouble(input.next());
            entry.setAmount(amount);
        } catch (NegativeAmountException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            enteredWrong("number format");
            changeAmount(entry);
        }
    }

    // MODIFIES: entry
    // EFFECTS: changes entry's category
    //          asks user to reenter category if their input is unacceptable
    private void changeCategory(Entry entry) {
        printCategories();
        System.out.println("\nNew category: ");
        String category = input.next();
        if (categories.contains(category)) {
            entry.setCategory(category);
        } else {
            enteredWrong("category");
            changeCategory(entry);
        }
    }

    // EFFECTS: displays available categories
    private void printCategories() {
        System.out.println("\nSelect from one of these categories:");
        System.out.println(String.join(", ", categories.getCategories()));
    }

    // EFFECTS: prints current entries
    private void printSpendingEntries() {
        System.out.println("\nHere's your list of entries: ");
        spendingList.getSpendingList().forEach(System.out::println);
    }

    // MODIFIES: this
    // EFFECTS: inits categories
    //          throws NameException if we try to add a blank category
    private void initCategories() throws NameException {
        categories = new Categories();
        for (String s : Arrays.asList("Groceries", "Clothing", "Travel")) {
            categories.addCategory(s);
        }
    }

    // MODIFIES: this
    // EFFECTS: inits entries
    //          throws NameException if title is blank
    //          throws NegativeAmountException if amount <= 0
    private void initEntries() throws NegativeAmountException, NameException {
        spendingList = new SpendingList();
        spendingList.addEntry(new Entry("Went to Montreal", 507.68, "Travel"));
        spendingList.addEntry(new Entry("Bought jeans", 68.98, "Clothing"));
        spendingList.addEntry(new Entry("Went to NoFrills", 70.67, "Groceries"));
    }

    // MODIFIES: this
    // EFFECTS: inits the input scanner
    private void initScanner() {
        input = new Scanner(System.in).useDelimiter("\n");
    }

    // EFFECTS: returns user input id,
    //          asks user to reenter id if they entered a wrong formatted number
    private int readEntryId() {
        System.out.println("\nEnter entry ID: ");
        try {
            return Integer.parseInt(input.next());
        } catch (NumberFormatException e) {
            enteredWrong("number format");
            printSpendingEntries();
            return readEntryId();
        }
    }

    // EFFECTS: prints types of sorting user can perform on entries
    private void printSortCommands() {
        System.out.println("\nHow do you want to sort entries?");
        System.out.println("am -> By amount spent (in descending order)");
        System.out.println("ca -> By category (in alphabetic order)");
        System.out.println("da -> By date (from newer to older)");
    }

    // EFFECTS: prints entry with given id and shows changes user can perform on that entry
    //          throws WrongIdException if entry with such id doesn't exist
    private void printChangeCommands() {
        System.out.println("ti -> Title");
        System.out.println("am -> Amount");
        System.out.println("ca -> Category");
        System.out.println(QUIT_COMMAND + " -> quit changing menu");
    }

    // EFFECTS: prints a message about what user entered wrong
    private void enteredWrong(String whatEnteredWrong) {
        System.out.println("\nYou entered a wrong " + whatEnteredWrong + "...");
    }
}
