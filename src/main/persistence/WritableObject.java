package persistence;

import org.json.JSONObject;

public interface WritableObject {
    // EFFECTS: returns this as JSON object
    JSONObject toJsonObject();
}
