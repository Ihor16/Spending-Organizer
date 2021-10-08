package ui;

import model.Categories;
import model.Entry;
import model.SpendingList;

import java.util.Arrays;
import java.util.Scanner;

public class SpendingApp {

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

            if (command.equals("q")) {
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
        System.out.println("q -> quit");
    }

    // MODIFIES: this
    // EFFECTS: processes user's menu commands
    private void processCommand(String command) {
        switch (command) {
            case "a":
                askInformationAboutNewEntry();
                break;
            case "r":
                removeEntryById();
                break;
            case "s":
                askAboutHowToSort();
                break;
            case "c":
                askWhichEntryToChange();
            default:
                enteredWrong("command");
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: changes specific aspects of the entry
    private void processChangingCommand(int id, String command) {
        switch (command) {
            case "ti":
                changeTitle(id);
                askWhatToChangeInEntry(id);
                break;
            case "am":
                changeAmount(id);
                askWhatToChangeInEntry(id);
                break;
            case "ca":
                changeCategory(id);
                askWhatToChangeInEntry(id);
                break;
            default:
                enteredWrong("changing command");
                askWhatToChangeInEntry(id);
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: processes user's sorting commands
    private void processSortingCommand(String command) {
        switch (command) {
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
                askAboutHowToSort();
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user to input id of the entry they want to change
    private void askWhichEntryToChange() {
        int id = readEntryId();
        if (spendingList.isValidId(id)) {
            askWhatToChangeInEntry(id);
        } else {
            enteredWrong("entry ID");
            printSpendingEntries();
            askWhichEntryToChange();
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user what they want to change in entry
    private void askWhatToChangeInEntry(int id) {
        System.out.println(spendingList.findById(id));
        System.out.println("\nWhat do you want to change?");
        System.out.println("ti -> Title");
        System.out.println("am -> Amount");
        System.out.println("ca -> Category");
        System.out.println("q -> quit changing menu");
        String command = input.next();
        if (!command.equals("q")) {
            processChangingCommand(id, input.next());
        }
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

    // MODIFIES: this
    // EFFECTS: asks user how they want to sort the entries
    private void askAboutHowToSort() {
        System.out.println("\nHow do you want to sort them?");
        System.out.println("am -> By amount spent (in descending order)");
        System.out.println("ca -> By category (in alphabetic order)");
        System.out.println("da -> By date (from newer to older)");
        String command = input.next();
        processSortingCommand(command);
    }

    // MODIFIES: this
    // EFFECTS: removes entry by its id if an entry with such id is found
    private void removeEntryById() {
        int id = readEntryId();
        if (!spendingList.removeById(id)) {
            enteredWrong("entry ID");
            printSpendingEntries();
            removeEntryById();
        }
    }

    // EFFECTS: returns user input id
    private int readEntryId() {
        System.out.println("\nEntry entry ID: ");
        return input.nextInt();
    }

    // MODIFIES: this
    // EFFECTS: user adds a new entry
    private void askInformationAboutNewEntry() {
        System.out.println("\nTitle: ");
        String title = input.next();
        System.out.println("\nAmount Spent: CAD ");
        double amount = input.nextDouble();
        printCategories();
        System.out.println("\nCategory: ");
        String category = input.next();
        addEntry(title, amount, category);
    }

    // MODIFIES: this
    // EFFECTS: user provides only category of the new entry
    private void askInformationAboutNewEntry(String title, double amount) {
        System.out.println("\nTitle: " + title);
        System.out.println("Amount Spent: CAD " + amount);
        System.out.println("Category: ");
        String category = input.next();
        addEntry(title, amount, category);
    }

    // MODIFIES: this
    // EFFECTS: if category is found in the set of categories, adds entry to the list
    //          asks user to reenter category otherwise
    private void addEntry(String title, double amount, String category) {
        if (categories.contains(category)) {
            spendingList.addEntry(new Entry(title, amount, category));
        } else {
            enteredWrong("category");
            printCategories();
            askInformationAboutNewEntry(title, amount);
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

    // EFFECTS: prints a message about what user entered wrong
    private void enteredWrong(String whatEnteredWrong) {
        System.out.println("\nYou entered a wrong " + whatEnteredWrong + "...");
    }
}
