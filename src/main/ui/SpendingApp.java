package ui;

import model.Record;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import model.exceptions.RecordFieldException;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.InvalidPathException;
import java.util.*;
import java.util.stream.Collectors;

// Class for UI console user interactions
public class SpendingApp {

    private static final String QUIT_COMMAND = "q";
    private SpendingList spendingList;
    private Scanner input;

    // EFFECTS: inits categories, records, scanner, and runs the app
    public SpendingApp() {
        try {
            initRecords();
            initScanner();
            runApp();
        } catch (RecordFieldException e) {
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

    // EFFECTS: displays current list of records and commands to operate on the list
    private void displayMenu() {
        printSpendingRecords();
        System.out.println("\nSelect one of these commands:");
        System.out.println("a -> add new record");
        System.out.println("c -> change existing record");
        System.out.println("r -> remove record");
        System.out.println("s -> sort records");
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
                addNewRecord(new Record());
                break;
            case "r":
                removeRecord();
                break;
            case "s":
                sortRecords();
                break;
            case "c":
                changeRecord();
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
    // EFFECTS: loads app state from file chosen by user
    private void loadFromFile() {
        askAboutSaving();
        String fileName = askFilename();
        if (fileName.isEmpty()) {
            System.out.println("\nYou don't have saved files...");
        } else {
            JsonReader reader = new JsonReader("./data/" + fileName);
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
    }

    // EFFECTS: returns filename user wants to load from
    private String askFilename() {
        List<String> files = getFilenames();
        if (files.isEmpty()) {
            return "";
        }
        System.out.println("\nSelect # of the file to load from");
        for (int i = 1; i <= files.size(); i++) {
            System.out.println(i + ": " + files.get(i - 1));
        }
        int number;
        String name;
        try {
            number = input.nextInt();
            name = files.get(number - 1);
        } catch (InputMismatchException | IndexOutOfBoundsException e) {
            return askFilename();
        }
        return name;
    }

    // EFFECTS: returns List<String> of all .json file names in
    //          ./data/ directory
    private List<String> getFilenames() throws NullPointerException {
        // Implementation of retrieving list of names is taken from
        // https://stackabuse.com/java-list-files-in-a-directory/
        File file = new File("./data");
        String[] files;
        try {
            files = Objects.requireNonNull(file.list());
        } catch (NullPointerException e) {
            return Collections.emptyList();
        }
        return Arrays.stream(files).filter(n -> n.contains(".json"))
                .collect(Collectors.toList());
    }

    // EFFECTS: shows already saved files if present,
    //          asks user where to save file and saves app's state to it,
    //          asks user to reenter filename to save to if they entered a wrong filename format
    private void saveToFile() {
        if (!getFilenames().isEmpty()) {
            System.out.println("Here are your saved files");
            getFilenames().forEach(f -> System.out.println(" - " + f));
        }
        System.out.println("Enter filename where you want to save your spending list, e.g., [filename]");
        String fileName = input.next();
        try (JsonWriter writer = new JsonWriter("./data/" + fileName + ".json")) {
            writer.open();
            writer.write(spendingList);
            System.out.println("Your file was saved to " + fileName + ".json");
        } catch (FileNotFoundException e) {
            enteredWrong("filename format");
            saveToFile();
        }
    }

    // MODIFIES: this and record
    // EFFECTS: asks user to enter each record field separately
    //          and populates record fields if user input is valid
    private void addNewRecord(Record record) {
        String title = addTitle(record);

        System.out.println("Title: " + title);
        double amount = addAmount(record);

        System.out.println("Title: " + title);
        System.out.println("Amount: " + amount);
        String category = addCategory(record);

        assert !(Objects.isNull(title) || Objects.isNull(category));
        spendingList.addRecord(record);
    }

    // MODIFIES: record
    // EFFECTS: asks user to enter category of financial record and returns the entered category,
    //          asks user to enter again if they enter a blank string
    private String addCategory(Record record) {
        System.out.println("\nAdd Category: ");
        printCategories();
        String category = input.next();
        try {
            record.setCategory(category);
            spendingList.addCategory(category);
            return record.getCategory();
        } catch (NameException e) {
            enteredWrong("string format");
            return addCategory(record);
        }
    }

    // MODIFIES: record
    // EFFECTS: asks user to enter amount of financial record and returns the entered amount,
    //          asks user to enter again if they enter not acceptable amount
    private double addAmount(Record record) {
        System.out.println("\nAdd Amount Spent in CAD");
        try {
            double amount = Double.parseDouble(input.next());
            record.setAmount(amount);
            return amount;
        } catch (NegativeAmountException e) {
            System.out.println("Error: " + e.getMessage());
            return addAmount(record);
        } catch (NumberFormatException e) {
            enteredWrong("number format");
            return addAmount(record);
        }
    }

    // MODIFIES: record
    // EFFECTS: asks user to enter title of financial record and returns the entered title,
    //          asks user to enter again if they enter not acceptable title
    private String addTitle(Record record) {
        System.out.println("\nAdd Title: ");
        try {
            String title = input.next();
            record.setTitle(title);
            return title;
        } catch (NameException e) {
            System.out.println("Error: " + e.getMessage());
            return addTitle(record);
        }
    }

    // MODIFIES: this
    // effects: asks used to choose record and removes it
    // EFFECTS: removes record by its id if a record with such id is found,
    //          asks user to enter id again otherwise
    private void removeRecord() {
        int index = readRecordIndex();
        Record chosenRecord = spendingList.getRecord(index);
        spendingList.removeRecord(chosenRecord);
    }

    // MODIFIES: this
    // EFFECTS: asks user how they want to sort the records and sorts them,
    //          asks user to reenter sorting command if they entered a wrong one
    private void sortRecords() {
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
                printSpendingRecords();
                sortRecords();
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user to input id of the record they want to change and specify the change,
    //          asks them to reenter id, if entered id isn't found
    private void changeRecord() {
        int index = readRecordIndex();
        Record foundRecord = spendingList.getRecord(index);
        System.out.println(foundRecord);
        printChangeCommands();
        String command = input.next();
        if (!command.equals(QUIT_COMMAND)) {
            processChange(foundRecord, command);
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user if they want to further change the record or quit the change menu,
    //          changes specified record field if user wants to change it
    // INVARIANT: record is valid
    private void changeRecord(Record record) {
        assert !(Objects.isNull(record.getTitle()) || Objects.isNull(record.getCategory()));
        System.out.println(record);
        printChangeCommands();
        String command = input.next();
        if (!command.equals(QUIT_COMMAND)) {
            processChange(record, command);
        }
    }

    // MODIFIES: record
    // EFFECTS: changes specific aspects of the record,
    //          and asks if user wants to change record further
    private void processChange(Record record, String command) {
        switch (command) {
            case "ti":
                changeTitle(record);
                break;
            case "am":
                changeAmount(record);
                break;
            case "ca":
                changeCategory(record);
                break;
            default:
                enteredWrong("changing command");
                break;
        }
        changeRecord(record);
    }

    // MODIFIES: record
    // EFFECTS: changes record's title,
    //          asks user to reenter title if their input is unacceptable
    private void changeTitle(Record record) {
        System.out.println("\nNew title:");
        try {
            String title = input.next();
            record.setTitle(title);
        } catch (NameException e) {
            System.out.println("Error: " + e.getMessage());
            changeTitle(record);
        }
    }

    // MODIFIES: record
    // EFFECTS: changes record's amount
    //          asks user to reenter amount if their input is unacceptable
    private void changeAmount(Record record) {
        System.out.println("\nNew amount: CAD");
        try {
            double amount = Double.parseDouble(input.next());
            record.setAmount(amount);
        } catch (NegativeAmountException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            enteredWrong("number format");
            changeAmount(record);
        }
    }

    // MODIFIES: record
    // EFFECTS: changes record's category
    //          asks user to reenter category if they enter a blank string
    private void changeCategory(Record record) {
        printCategories();
        System.out.println("\nChanged category: ");
        String category = input.next();
        try {
            record.setCategory(category);
            spendingList.addCategory(category);
        } catch (NameException e) {
            enteredWrong("string format");
            changeCategory(record);
        }
    }

    // EFFECTS: displays available categories
    private void printCategories() {
        System.out.println("\nSelect from one of these categories or add a new one:");
        System.out.println(String.join("\n", spendingList.getCategories()));
    }

    // EFFECTS: prints current records
    private void printSpendingRecords() {
        System.out.println("\nHere's your list of records: ");
        int index = 1;
        for (Record record : spendingList.getRecords()) {
            System.out.println(index + ": " + record);
            index++;
        }
    }

    // MODIFIES: this
    // EFFECTS: inits records with slight delay between each one,
    //          throws NameException if title is blank
    //          throws NegativeAmountException if amount <= 0
    private void initRecords() throws NegativeAmountException, NameException {
        spendingList = new SpendingList();
        try {
            spendingList.addRecord(new Record("Went to Montreal", 507.68, "Travel"));
            Thread.sleep(10);
            spendingList.addRecord(new Record("Bought jeans", 68.98, "Clothing"));
            Thread.sleep(10);
            spendingList.addRecord(new Record("Went to NoFrills", 70.67, "Groceries"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: inits the input scanner
    private void initScanner() {
        input = new Scanner(System.in).useDelimiter("\n");
    }

    // EFFECTS: returns index of a record that user chose,
    //          asks user to reenter index if they entered a wrong-formatted number
    //          or there's no record with such index
    private int readRecordIndex() {
        System.out.println("\nEnter record Index: ");
        try {
            int index = Integer.parseInt(input.next());
            --index;
            spendingList.getRecord(index);
            return index;
        } catch (NumberFormatException e) {
            enteredWrong("number format");
            printSpendingRecords();
            return readRecordIndex();
        } catch (IndexOutOfBoundsException e) {
            enteredWrong("record index");
            printSpendingRecords();
            return readRecordIndex();
        }
    }

    // EFFECTS: prints types of sorting user can perform on records
    private void printSortCommands() {
        System.out.println("\nHow do you want to sort records?");
        System.out.println("am -> By amount spent (in descending order)");
        System.out.println("ca -> By category (in alphabetic order)");
        System.out.println("da -> By date (from newer to older)");
    }

    // EFFECTS: prints record with given id and shows changes user can perform on that record
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
