package persistence;

import model.SpendingList;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Represents writer that creates a JSON file from provided financial entries list
// Implementation of the class is based on the JsonWriter class from JsonSerializationDemo
public class JsonWriter implements AutoCloseable {
    private static final int TAB = 2;
    private PrintWriter writer;
    private final String destination;

    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer,
    //          throws FileNotFoundException if it can't open file with destination
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(destination);
    }

    // MODIFIES: this
    // EFFECTS: writes spendingList to a JSON file and saves it
    public void write(SpendingList spendingList) {
        JSONObject json = spendingList.toJsonObject();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: saves json to file at the destination
    private void saveToFile(String json) {
        writer.print(json);
    }

    @Override
    // MODIFIES: this
    // EFFECTS: closes the writer
    public void close() {
        writer.close();
    }
}
