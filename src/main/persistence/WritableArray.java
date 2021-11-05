package persistence;

import org.json.JSONArray;

public interface WritableArray {
    // EFFECTS: returns this as JSON Array
    JSONArray toJsonArray();
}
