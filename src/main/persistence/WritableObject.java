package persistence;

import org.json.JSONObject;

// Classes that implement it can be written to a json object
public interface WritableObject {
    // EFFECTS: returns this as JSON Object
    JSONObject toJsonObject();
}
