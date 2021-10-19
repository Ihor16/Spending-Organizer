package persistence;

import model.Categories;
import model.Entry;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// Represents reader that reads spending list and categories from provided JSON file
// Implementation of the class is based on the JsonWriter class from JsonSerializationDemo
public class JsonReader {
    private final String path;
    private JSONObject json;

    // EFFECTS: creates a new reader with specified source to read file from
    public JsonReader(String path) {
        this.path = path;
    }

    // EFFECTS: reads file content,
    //          throws IOException if there's an error while reading the file
    public void openFile() throws IOException {
        String fileContent = getFileContent(path);
        json = convertToJsonObject(fileContent);

    }

    // EFFECTS: returns SpendingList from source JSON file,
    //          throws NameException, NegativeAmountException if Entries in json file are corrupted
    public SpendingList readSpendingList() throws NameException, NegativeAmountException {
        JSONArray spendingJsonArray = getJsonArrayByKey("spending list");
        SpendingList spendingList = new SpendingList();
        parseEntry(spendingList, spendingJsonArray);
        return spendingList;
    }

    // MODIFIES: spendingList
    // EFFECTS: parses spendingJsonArray and populates spendingList with its Entries,
    //          and sorts the result spendingList by date
    private void parseEntry(SpendingList spendingList, JSONArray spendingJsonArray)
            throws NameException, NegativeAmountException {
        for (int i = 0; i < spendingJsonArray.length(); i++) {
            Entry entry = new Entry();
            JSONObject jsonEntry = spendingJsonArray.getJSONObject(i);
            entry.setTitle(jsonEntry.getString("title"));
            entry.setAmount(jsonEntry.getDouble("amount"));
            entry.setCategory(jsonEntry.getString("category"));
            entry.setTimeAdded(jsonEntry.getString("timeAdded"));
            spendingList.addEntry(entry);
        }
        spendingList.sortByDate();
    }

    // EFFECTS: returns Categories from source JSON file,
    //          throws NameException if one of the categories is corrupted
    public Categories readCategories() throws NameException {
        JSONArray categoriesJsonArray = getJsonArrayByKey("categories");

        Categories categories = new Categories();
        for (Object category : categoriesJsonArray) {
            categories.addCategory((String) category);
        }
        return categories;
    }

    // EFFECTS: returns content of file from the path
    private String getFileContent(String path) throws IOException {
        // Implementation is taken from
        // https://www.youtube.com/watch?v=6IGl4Tf2VVI
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    // REQUIRES: JSONArray with key exists in json
    // EFFECTS: searches json for JSONArray with provided key,
    //          and returns found JSONArray
    private JSONArray getJsonArrayByKey(String key) {
        return json.getJSONArray(key);
    }

    // EFFECTS: returns JSONObject from the provided content
    private JSONObject convertToJsonObject(String content) {
        return new JSONObject(content);
    }
}
