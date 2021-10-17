package ui;

import model.Categories;
import model.Entry;
import model.SpendingList;
import model.exceptions.*;

import java.util.Arrays;
import java.util.Scanner;

public class SpendingApp {

    private static final String QUIT_COMMAND = "q";
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
                addEntry();
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

    // MODIFIES: this
    // EFFECTS: adds entry to the list if category is found in the set of categories and amount > 0
    //          asks user to reenter category otherwise
    private void addEntry() {
        try {
            Entry entry = new Entry();
            System.out.println("\nAdd Title: ");
            String title = input.next();
            entry.setTitle(title);

            System.out.println("\nAdd Amount Spent: CAD ");
            double amount = input.nextDouble();
            entry.setAmount(amount);

            System.out.println("\nAdd Category: ");
            printCategories();
            String category = input.next();
            entry.setCategory(category);

            spendingList.addEntry(entry);
        } catch (NameException | NegativeAmountException | NonExistentCategoryException e) {
            System.out.println("Error: " + e.getMessage());
            addEntry();
        }
    }

    // MODIFIES: this
    // EFFECTS: removes entry by its id if an entry with such id is found
    private void removeEntry() {
        int id = readEntryId();
        try {
            spendingList.removeById(id);
        } catch (NonExistentIdException e) {
            System.out.println("Error: " + e.getMessage());
            removeEntry();
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user how they want to sort the entries and sorts them
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
    // EFFECTS: asks user to input id of the entry they want to change and specify the change
    //          if id isn't found, asks them to reenter it
    private void changeEntry() {
        int id = readEntryId();
        try {
            printChangeCommands(id);
            String command = input.next();
            if (!command.equals(QUIT_COMMAND)) {
                processChange(spendingList.findById(id), command);
            }
        } catch (NonExistentIdException e) {
            System.out.println("Error: " + e.getMessage());
            printSpendingEntries();
            changeEntry();
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user to input id of the entry they want to change and specify the change
    //          if id isn't found, asks them to reenter it
    private void changeEntry(Entry entry) {
        try {
            printChangeCommands(entry.getId());
            String command = input.next();
            if (!command.equals(QUIT_COMMAND)) {
                processChange(entry, command);
            }
        } catch (NonExistentIdException e) {
            System.out.println("Error: " + e.getMessage());
            printSpendingEntries();
            changeEntry();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes specific aspects of the entry
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

    // MODIFIES: this
    // EFFECTS: changes entry's title
    private void changeTitle(Entry entry) {
        System.out.println("\nNew title:");
        String title = input.next();
        try {
            entry.setTitle(title);
        } catch (NameException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // MODIFIES: this
    // EFFECTS: changes entry's amount
    private void changeAmount(Entry entry) {
        System.out.println("\nNew amount: CAD");
        double amount = input.nextDouble();
        try {
            entry.setAmount(amount);
        } catch (NegativeAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // MODIFIES: this
    // EFFECTS: changes entry's category
    private void changeCategory(Entry entry) {
        printCategories();
        System.out.println("\nNew category: ");
        String category = input.next();
        try {
            entry.setCategory(category);
        } catch (NonExistentCategoryException | NameException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // EFFECTS: displays available categories
    private void printCategories() {
        System.out.println("\nSelect from one of these categories:");
        System.out.println(String.join(", ", Categories.getCategories()));
    }

    // EFFECTS: prints current entries
    private void printSpendingEntries() {
        System.out.println("\nHere's your list of entries: ");
        spendingList.getSpendingList().forEach(System.out::println);
    }

    // MODIFIES: this
    // EFFECTS: inits categories
    private void initCategories() throws NameException {
        for (String s : Arrays.asList("Groceries", "Clothing", "Travel")) {
            Categories.addCategory(s);
        }
    }

    // MODIFIES: this
    // EFFECTS: inits entries
    private void initEntries() throws NegativeAmountException,
            NameException, NonExistentCategoryException {
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

    // EFFECTS: returns user input id
    private int readEntryId() {
        System.out.println("\nEnter entry ID: ");
        return input.nextInt();
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
    private void printChangeCommands(int id) throws NonExistentIdException {
        System.out.println(spendingList.findById(id));
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
