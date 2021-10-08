package ui;

import model.Categories;
import model.Entry;
import model.SpendingList;

import java.util.Arrays;
import java.util.Scanner;

public class SpendingApp {

    private static final String QUIT_COMMAND = "q";
    private SpendingList spendingList;
    private Categories categories;
    private Scanner input;

    public SpendingApp() {
        initCategories();
        initEntries();
        initScanner();

        runApp();
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
    // EFFECTS: adds entry to the list if category is found in the set of categories
    //          asks user to reenter category otherwise
    private void addEntry() {
        System.out.println("\nTitle: ");
        String title = input.next();
        System.out.println("\nAmount Spent: CAD ");
        double amount = input.nextDouble();
        printCategories();
        System.out.println("\nCategory: ");
        String category = input.next();

        if (categories.contains(category)) {
            spendingList.addEntry(new Entry(title, amount, category));
        } else {
            enteredWrong("category");
            printCategories();
            addEntry(title, amount);
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user to enter category
    private void addEntry(String title, double amount) {
        System.out.println("\nTitle: " + title);
        System.out.println("Amount Spent: CAD " + amount);
        System.out.println("Category: ");
        String category = input.next();

        if (categories.contains(category)) {
            spendingList.addEntry(new Entry(title, amount, category));
        } else {
            enteredWrong("category");
            printCategories();
            addEntry(title, amount);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes entry by its id if an entry with such id is found
    private void removeEntry() {
        int id = readEntryId();
        if (!spendingList.removeById(id)) {
            enteredWrong("entry ID");
            printSpendingEntries();
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
        if (spendingList.isValidId(id)) {
            printChangeCommands(id);
            String command = input.next();
            if (!command.equals(QUIT_COMMAND)) {
                processChange(id, command);
            }
        } else {
            enteredWrong("entry ID");
            printSpendingEntries();
            changeEntry();
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user to if they want to further change same entry
    private void changeEntry(int id) {
        printChangeCommands(id);
        String command = input.next();
        if (!command.equals(QUIT_COMMAND)) {
            processChange(id, command);
        }
    }

    // MODIFIES: this
    // EFFECTS: changes specific aspects of the entry
    private void processChange(int id, String command) {
        switch (command) {
            case "ti":
                changeTitle(id);
                break;
            case "am":
                changeAmount(id);
                break;
            case "ca":
                changeCategory(id);
                break;
            default:
                enteredWrong("changing command");
                break;
        }
        changeEntry(id);
    }

    // MODIFIES: this
    // EFFECTS: changes entry's title
    private void changeTitle(int id) {
        System.out.println("\nNew title:");
        String title = input.next();
        spendingList.findById(id).setTitle(title);
    }

    // MODIFIES: this
    // EFFECTS: changes entry's amount
    private void changeAmount(int id) {
        System.out.println("\nNew amount: CAD");
        double amount = input.nextDouble();
        spendingList.findById(id).setAmount(amount);
    }

    // MODIFIES: this
    // EFFECTS: changes entry's category
    private void changeCategory(int id) {
        System.out.println("\nNew category: ");
        String category = input.next();
        if (categories.contains(category)) {
            spendingList.findById(id).setCategory(category);
        } else {
            enteredWrong("category");
            printCategories();
            changeCategory(id);
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
    private void initCategories() {
        categories = new Categories();
        Arrays.asList("Groceries", "Clothing", "Travel")
                .forEach(categories::addCategory);
    }

    // MODIFIES: this
    // EFFECTS: inits entries
    private void initEntries() {
        spendingList = new SpendingList();
        spendingList.addEntry(new Entry("Went to Montreal", 507.68, "Travel"));
        spendingList.addEntry(new Entry("Bought jeans", 68.98, "Clothing"));
        spendingList.addEntry(new Entry("Went to NoFrills", 70.67, "Groceries"));
    }

    // MODIFIES: this
    // EFFECTS: inits the input scanner
    private void initScanner() {
        input = new Scanner(System.in);
    }

    // EFFECTS: returns user input id
    private int readEntryId() {
        System.out.println("\nEntry entry ID: ");
        return input.nextInt();
    }

    // EFFECTS: prints types of sorting user can perform on entries
    private void printSortCommands() {
        System.out.println("\nHow do you want to sort entries?");
        System.out.println("am -> By amount spent (in descending order)");
        System.out.println("ca -> By category (in alphabetic order)");
        System.out.println("da -> By date (from newer to older)");
    }

    // EFFECTS: prints types of changes user can perform on entries
    private void printChangeCommands(int id) {
        System.out.println(spendingList.findById(id));
        System.out.println("\nWhat do you want to change?");
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
