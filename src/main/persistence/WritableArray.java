package persistence;

import org.json.JSONArray;

// Classes that implement it can be written to a json array
public interface WritableArray {
    // EFFECTS: returns this as JSON Array
    JSONArray toJsonArray();
}
