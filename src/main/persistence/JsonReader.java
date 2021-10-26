package persistence;

import model.Record;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        SpendingList spendingList = new SpendingList();
        parseRecord(spendingList);
        parseCategory(spendingList);
        return spendingList;
    }

    // MODIFIES: spendingList
    // EFFECTS: adds categories from json to spendingList
    //          throws NameException if categories in the file are corrupted
    private void parseCategory(SpendingList spendingList) throws NameException {
        JSONArray jsonArray = json.getJSONArray("categories");
        for (Object obj : jsonArray) {
            spendingList.addCategory((String) obj);
        }
    }

    // MODIFIES: spendingList
    // EFFECTS: adds records from json to spendingList
    //          throws NameException or NegativeAmountException if records in the file are corrupted
    private void parseRecord(SpendingList spendingList) throws NameException, NegativeAmountException {
        JSONArray jsonArray = json.getJSONArray("records");
        for (int i = 0; i < jsonArray.length(); i++) {
            Record record = new Record();
            JSONObject jsonRecord = jsonArray.getJSONObject(i);
            record.setTitle(jsonRecord.getString("title"));
            record.setAmount(jsonRecord.getDouble("amount"));
            record.setCategory(jsonRecord.getString("category"));
            record.setTimeAdded(jsonRecord.getString("timeAdded"));
            spendingList.addRecord(record);
        }
        spendingList.sortByDate();
    }
}
