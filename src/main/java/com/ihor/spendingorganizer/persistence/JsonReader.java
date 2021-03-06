package com.ihor.spendingorganizer.persistence;

import com.ihor.spendingorganizer.model.Categories;
import com.ihor.spendingorganizer.model.Category;
import com.ihor.spendingorganizer.model.Record;
import com.ihor.spendingorganizer.model.SpendingList;
import com.ihor.spendingorganizer.model.exceptions.NameException;
import com.ihor.spendingorganizer.model.exceptions.NegativeAmountException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

// Represents reader that reads SpendingList from provided JSON file
// Implementation of the class is based on the JsonReader class from JsonSerializationDemo
public class JsonReader {
    private JSONObject json;
    private final String path;

    public JsonReader(String path) {
        this.path = path;
    }

    // MODIFIES: this
    // EFFECTS: returns SpendingList from the file,
    //          throws IOException if there's an error while reading file from the path,
    //          throws NegativeAmountException or NameException if file is corrupted
    public SpendingList read() throws IOException, NegativeAmountException, NameException {
        String fileContents = getFileContents();
        json = new JSONObject(fileContents);
        return readSpendingList();
    }

    // EFFECTS: returns contents of file from the path as a String
    //          throws IOException if there's a problem reading the file from path
    private String getFileContents() throws IOException {
        // Implementation is taken from
        // https://www.youtube.com/watch?v=6IGl4Tf2VVI
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    // EFFECTS: returns SpendingList from json,
    //          throws NameException or NegativeAmountException if file is corrupted
    private SpendingList readSpendingList() throws NameException, NegativeAmountException {
        SpendingList spendingList = parseCategories();
        parseRecords(spendingList);
        removeDuplicateDefaultCategories(spendingList);
        return spendingList;
    }

    // MODIFIES: spendingList
    // EFFECTS: removes automatically created default category from categories
    private void removeDuplicateDefaultCategories(SpendingList spendingList) {
        List<Category> defaultCategories = spendingList.getCategories().getCategories()
                .filtered(Category::isDefault);

        if (defaultCategories.size() == 2) {
            spendingList.getCategories().getCategories().removeIf(c -> c.getName().equals("default"));
            spendingList.getCategories().setDefaultCategory(defaultCategories.get(0));
        }
    }

    // MODIFIES: spendingList
    // EFFECTS: adds categories from json to spendingList
    private SpendingList parseCategories() throws NameException {
        JSONArray jsonArray = json.getJSONArray("categories");
        Categories categories = new Categories();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonCategory = jsonArray.getJSONObject(i);
            new Category(jsonCategory.getString("name"), categories,
                    jsonCategory.getBoolean("isShown"), jsonCategory.getBoolean("isDefault"));
        }
        return new SpendingList(categories);
    }

    // MODIFIES: spendingList
    // EFFECTS: adds records from json to spendingList
    //          throws NameException or NegativeAmountException if records in the file are corrupted
    private void parseRecords(SpendingList spendingList) throws NameException, NegativeAmountException {
        JSONArray jsonArray = json.getJSONArray("records");
        Categories categories = spendingList.getCategories();
        for (int i = 0; i < jsonArray.length(); i++) {
            Record record = new Record();
            JSONObject jsonRecord = jsonArray.getJSONObject(i);
            record.setTitle(jsonRecord.getString("title"));
            record.setAmount(jsonRecord.getDouble("amount"));
            record.setCategory(categories.getCategoryByName(jsonRecord.getJSONObject("category").getString("name")));
            record.setTimeAdded(jsonRecord.getString("timeAdded"));
            spendingList.addRecord(record);
        }
        spendingList.getRecords()
                .sort(Comparator.comparing(Record::getTimeAdded).reversed());
    }
}
