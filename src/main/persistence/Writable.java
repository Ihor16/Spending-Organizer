package persistence;

import org.json.JSONObject;

// Classes that implement it cat bo written to a json file
public interface Writable {
    // EFFECTS: returns this as JSON Object
    JSONObject toJsonObject();
}
