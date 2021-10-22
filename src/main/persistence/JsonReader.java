package persistence;

import model.Entry;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// Represents reader that reads spending list from provided JSON file
// Implementation of the class is based on the JsonReader class from JsonSerializationDemo
public class JsonReader {
    private JSONObject json;

    public JsonReader(String path) throws IOException {
        read(path);
    }

    // EFFECTS: assigns file's content to json
    //          throws IOException if there's an error while reading file from the path
    public void read(String path) throws IOException {
        String fileContent = getFileContent(path);
        json = new JSONObject(fileContent);
    }

    // EFFECTS: returns content of file from the path as a String
    //          throws IOException if there's a problem reading the file from path
    private String getFileContent(String path) throws IOException {
        // Implementation is taken from
        // https://www.youtube.com/watch?v=6IGl4Tf2VVI
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    // EFFECTS: returns SpendingList from json,
    //          throws NameException, NegativeAmountException if file is corrupted
    public SpendingList readSpendingList() throws NameException, NegativeAmountException {
        SpendingList spendingList = new SpendingList();
        parseEntry(spendingList);
        parseCategory(spendingList);
        return spendingList;
    }

    // MODIFIES: spendingList
    // EFFECTS: adds categories from the file to spendingList
    private void parseCategory(SpendingList spendingList) throws NameException {
        JSONArray jsonArray = json.getJSONArray("categories");
        for (Object o : jsonArray) {
            spendingList.addCategory((String) o);
        }
    }

    // MODIFIES: spendingList
    // EFFECTS: adds records from the file to spendingList
    private void parseEntry(SpendingList spendingList) throws NameException, NegativeAmountException {
        JSONArray jsonArray = getJsonArrayByKey("records");
        for (int i = 0; i < jsonArray.length(); i++) {
            Entry entry = new Entry();
            JSONObject jsonEntry = jsonArray.getJSONObject(i);
            entry.setTitle(jsonEntry.getString("title"));
            entry.setAmount(jsonEntry.getDouble("amount"));
            entry.setCategory(jsonEntry.getString("category"));
            entry.setTimeAdded(jsonEntry.getString("timeAdded"));
            spendingList.addEntry(entry);
        }
        spendingList.sortByDate();
    }

    // REQUIRES: JSONArray with key exists in json
    // EFFECTS: searches json for JSONArray with key and returns this array
    private JSONArray getJsonArrayByKey(String key) {
        return json.getJSONArray(key);
    }
}
