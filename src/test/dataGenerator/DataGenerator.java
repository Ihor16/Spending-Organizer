package dataGenerator;

import javafx.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Represents a class for generating large pieces of random data for demo json files
// This generates and prints only data, so use VIM Macros to format it to Json
public class DataGenerator {

    private final List<Pair<String, List<String>>> categoryTitles;
    private List<LocalDateTime> dates;
    private final Random random;

    public DataGenerator() {
        categoryTitles = new ArrayList<>();
        dates = new ArrayList<>();
        random = new Random();
        populateCategoryTitles();
    }

    public static void main(String[] args) {
        DataGenerator g = new DataGenerator();
        int numOfRecords = 100;
        g.generateDates(numOfRecords);

        g.printRecords(500);
        System.out.println("---CATEGORIES---");
        g.printCategories();
    }

    // EFFECTS: prints a random list of records that have amountSpent <= maxAmount
    private void printRecords(int maxAmount) {
        StringBuilder builder = new StringBuilder();

        IntStream.range(0, dates.size()).forEach(i -> {
            Pair<String, List<String>> pair = selectPair();
            String title = selectTitle(pair);

            String result = "\n" + random.nextInt(maxAmount)
                    + "\n" + dates.get(i)
                    + "\n" + title
                    + "\n" + pair.getKey();

            builder.append(result);
        });
        System.out.println(builder);
    }

    // EFFECTS: prints generated categories
    private void printCategories() {
        StringBuilder builder = new StringBuilder();
        categoryTitles.forEach(p -> builder.append("\n").append(p.getKey()));
        System.out.println(builder);
    }

    // EFFECTS: returns a randomly selected String from pair values list
    private String selectTitle(Pair<String, List<String>> pair) {
        return pair.getValue().get(random.nextInt(pair.getValue().size()));
    }

    // EFFECTS: returns a randomly selected pair from categoryTitle list
    private Pair<String, List<String>> selectPair() {
        return categoryTitles.get(random.nextInt(categoryTitles.size()));
    }

    // MODIFIES: this
    // EFFECTS: generates n dates, and populate dates list with these
    //          this list contains only distinct dates, and it is sorted (from more recent to less recent date)
    //          if generated dates contain duplicate, just keep one of them, so list size is <= n
    private void generateDates(int numOfRecords) {
        IntStream.range(0, numOfRecords).forEach(n -> {
            LocalDate localDate = LocalDate.of(2020 + random.nextInt(2),
                    1 + random.nextInt(12),
                    1 + random.nextInt(28));
            LocalTime localTime = LocalTime.of(random.nextInt(24),
                    1 + random.nextInt(59),
                    1 + random.nextInt(59));
            dates.add(LocalDateTime.of(localDate, localTime));
        });
        dates.sort(Comparator.reverseOrder());
        dates = dates.stream().distinct().collect(Collectors.toList());
    }

    // MODIFIES: this
    // EFFECTS: adds a new entry to the categoryTitle list
    private void addCategoryTitles(String category, List<String> titles) {
        categoryTitles.add(new Pair<>(category, titles));
    }

    // MODIFIES: this
    // EFFECTS: populates categoryTitle list
    private void populateCategoryTitles() {
        addCategoryTitles("Groceries", Arrays.asList("Went to SaveOn", "Went to NoFrills"));
        addCategoryTitles("Travel", Arrays.asList("Montreal", "Victoria", "Toronto"));
        addCategoryTitles("Clothing", Arrays.asList("Jeans", "T-Shirt", "Shorts", "Winter coat"));
        addCategoryTitles("Entertainment", Arrays.asList("Theatre", "Cinema", "Restaurant with friends"));
        addCategoryTitles("School", Arrays.asList("Textbooks", "Club fee"));
        addCategoryTitles("Housing", Collections.singletonList("Housing fee"));
    }
}
