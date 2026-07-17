package com.main.CoreWorks.Resources;

import com.badlogic.gdx.utils.*;

public class ResourceTemplate {
    private final JsonValue templateStats;

    public ResourceTemplate(JsonValue data) {
        templateStats = data;
    }

    public Resource of() {
        return new Resource(templateStats);
    }

    public Resource of(ObjectMap<String, Modifier> mods) {
        return new Resource(templateStats, mods);
    }

    public String getName() {
        return templateStats.getString("Name");
    }

    public JsonValue getData() {
        return templateStats;
    }
}
