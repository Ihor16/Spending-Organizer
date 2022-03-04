package com.ihor.spendingorganizer.persistence;

import com.ihor.spendingorganizer.model.SpendingList;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Represents writer that creates a JSON file from provided SpendingList
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
    //          throws FileNotFoundException if file with path can't be found
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(destination);
    }

    // MODIFIES: this
    // EFFECTS: writes SpendingList to a JSON file and saves it
    public void write(SpendingList spendingList) {
        JSONObject json = spendingList.toJsonObject();
        writer.print(json.toString(TAB));
    }

    @Override
    // MODIFIES: this
    // EFFECTS: closes the writer
    public void close() {
        writer.close();
    }
}
