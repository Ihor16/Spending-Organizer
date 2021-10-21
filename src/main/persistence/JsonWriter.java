//package persistence;
//
//import model.Categories;
//import model.SpendingList;
//import org.json.JSONObject;
//
//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//
//// Represents writer that creates a JSON file from provided financial entries list and categories
//// Implementation of the class is based on the JsonWriter class from JsonSerializationDemo
//public class JsonWriter implements AutoCloseable {
//    private static final int TAB = 2;
//    private PrintWriter writer;
//    private final String destination;
//
//    // EFFECTS: creates a writer with specified location of the file to write to
//    public JsonWriter(String destination) {
//        this.destination = destination;
//    }
//
//    // MODIFIES: this
//    // EFFECTS: opens file writer,
//    //          throws FileNotFoundException if destination is a file with unacceptable name
//    public void open() throws FileNotFoundException {
//        writer = new PrintWriter(destination);
//    }
//
//    // MODIFIES: this
//    // EFFECTS: writes provided objects to a JSON file
//    public void write(SpendingList spendingList, Categories categories) {
//        JSONObject jsonObject = toJsonObject(spendingList, categories);
//        saveToFile(jsonObject.toString(TAB));
//    }
//
//    // MODIFIES: this
//    // EFFECTS: writes provided string to file
//    private void saveToFile(String json) {
//        writer.print(json);
//    }
//
//    @Override
//    // EFFECTS: closes the writer
//    public void close() {
//        writer.close();
//    }
//
//    // EFFECTS: returns a JSON object containing provided spending list and categories
//    private JSONObject toJsonObject(SpendingList spendingList, Categories categories) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("spending list", spendingList.toJsonArray());
//        jsonObject.put("categories", categories.toJsonArray());
//        return jsonObject;
//    }
//}
