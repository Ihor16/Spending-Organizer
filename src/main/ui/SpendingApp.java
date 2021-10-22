package ui;

import model.Entry;
import model.SpendingList;
import model.exceptions.EntryFieldException;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.nio.file.InvalidPathException;
import java.util.Objects;
import java.util.Scanner;

// Class for UI console user interactions
public class SpendingApp {

    private static final String QUIT_COMMAND = "q";
    private SpendingList spendingList;
    private Scanner input;

    // EFFECTS: inits categories, entries, scanner, and runs the app
    public SpendingApp() {
        try {
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
                askAboutSaving();
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
        System.out.println("r -> removeEntry entry");
        System.out.println("s -> sort entries");
        System.out.println("l -> load spending list from file");
        System.out.println("p -> save your changes");
        System.out.println(QUIT_COMMAND + " -> quit");
    }

    // EFFECTS: asks user if they want to save their file
    private void askAboutSaving() {
        System.out.println("Do you want to save your changes? [Y/N]");
        String answer = input.next();
        switch (answer) {
            case "Y":
                saveToFile();
                break;
            case "N":
                System.out.println("Your file wasn't saved");
                break;
            default:
                enteredWrong("saving command");
                askAboutSaving();
        }
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
            case "l":
                loadFromFile();
                break;
            case "p":
                saveToFile();
                break;
            default:
                enteredWrong("command");
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user to enter a filename they want to load from,
    //          and loads app state from that file
    private void loadFromFile() {
        System.out.println("Enter file name, e.g, [filename]");
        String fileName = input.next();
        JsonReader reader = new JsonReader("./data/" + fileName + ".json");
        try {
            spendingList = reader.read();
        } catch (NameException | NegativeAmountException e) {
            System.out.println("The file you're trying to open is corrupted...");
        } catch (InvalidPathException e) {
            enteredWrong("file path");
        } catch (Exception e) {
            System.out.println("There's an error loading your file...");
        }
    }

    // EFFECTS: asks user where to save file and saves app's state to it
    private void saveToFile() {
        System.out.println("Enter filename where you want to save your spending list, e.g., [filename]");
        String fileName = input.next();
        try (JsonWriter writer = new JsonWriter("./data/" + fileName + ".json")) {
            writer.open();
            writer.write(spendingList);
            System.out.println("Your file was saved to " + fileName + ".json");
        } catch (FileNotFoundException e) {
            enteredWrong("filename format");
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
    //          asks user to enter again if they enter a blank string
    private String addCategory(Entry entry) {
        System.out.println("\nAdd Category: ");
        printCategories();
        String category = input.next();
        try {
            entry.setCategory(category);
            spendingList.addCategory(category);
            return entry.getCategory();
        } catch (NameException e) {
            enteredWrong("string format");
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
    // effects: asks used to choose entry and removes it
    // EFFECTS: removes entry by its id if an entry with such id is found,
    //          asks user to enter id again otherwise
    private void removeEntry() {
        int index = readEntryIndex();
        Entry chosenEntry = spendingList.getEntry(index);
        spendingList.removeEntry(chosenEntry);
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
        int index = readEntryIndex();
        Entry foundEntry = spendingList.getEntry(index);
        System.out.println(foundEntry);
        printChangeCommands();
        String command = input.next();
        if (!command.equals(QUIT_COMMAND)) {
            processChange(foundEntry, command);
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
    //          asks user to reenter category if they enter a blank string
    private void changeCategory(Entry entry) {
        printCategories();
        System.out.println("\nChanged category: ");
        String category = input.next();
        try {
            entry.setCategory(category);
            spendingList.addCategory(category);
        } catch (NameException e) {
            enteredWrong("string format");
            changeCategory(entry);
        }
    }

    // EFFECTS: displays available categories
    private void printCategories() {
        System.out.println("\nSelect from one of these categories or add a new one:");
        System.out.println(String.join(", ", spendingList.getCategories()));
    }

    // EFFECTS: prints current entries
    private void printSpendingEntries() {
        System.out.println("\nHere's your list of entries: ");
        int index = 1;
        for (Entry entry : spendingList.getSpendingList()) {
            System.out.println(index + ": " + entry);
            index++;
        }
    }

    // MODIFIES: this
    // EFFECTS: inits entries with slight delay between each one,
    //          throws NameException if title is blank
    //          throws NegativeAmountException if amount <= 0
    private void initEntries() throws NegativeAmountException, NameException {
        spendingList = new SpendingList();
        try {
            spendingList.addEntry(new Entry("Went to Montreal", 507.68, "Travel"));
            Thread.sleep(10);
            spendingList.addEntry(new Entry("Bought jeans", 68.98, "Clothing"));
            Thread.sleep(10);
            spendingList.addEntry(new Entry("Went to NoFrills", 70.67, "Groceries"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: inits the input scanner
    private void initScanner() {
        input = new Scanner(System.in).useDelimiter("\n");
    }

    // EFFECTS: returns index of an entry that user chose,
    //          asks user to reenter index if they entered a wrong-formatted number
    //          or there's no entry with such index
    private int readEntryIndex() {
        System.out.println("\nEnter entry Index: ");
        try {
            int index = Integer.parseInt(input.next());
            --index;
            spendingList.getEntry(index);
            return index;
        } catch (NumberFormatException e) {
            enteredWrong("number format");
            printSpendingEntries();
            return readEntryIndex();
        } catch (IndexOutOfBoundsException e) {
            enteredWrong("entry index");
            printSpendingEntries();
            return readEntryIndex();
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
