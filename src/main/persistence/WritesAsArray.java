package persistence;

import org.json.JSONArray;

public interface WritesAsArray {
    // EFFECTS: returns this as JSON array
    JSONArray toJsonArray();
}
