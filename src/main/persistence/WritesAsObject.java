package persistence;

import org.json.JSONObject;

public interface WritesAsObject {
    // EFFECTS: returns this as JSON object
    JSONObject toJsonObject();
}
