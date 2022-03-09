package com.ihor.spendingorganizer.controllers.holders;

import com.ihor.spendingorganizer.controllers.enums.SceneEnum;
import javafx.scene.Scene;

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
