package ui.controllers.holders;

import javafx.scene.Scene;
import ui.controllers.enums.SceneEnum;

import java.util.HashMap;
import java.util.Map;

// Represents a Singleton holder of scenes
public final class SceneHolder {

    private final Map<SceneEnum, Scene> sceneMap = new HashMap<>();
    private static final SceneHolder INSTANCE = new SceneHolder();

    private SceneHolder() {
    }

    // EFFECTS: returns instance of this
    public static SceneHolder getInstance() {
        return INSTANCE;
    }

    public Map<SceneEnum, Scene> getSceneMap() {
        return sceneMap;
    }
}
