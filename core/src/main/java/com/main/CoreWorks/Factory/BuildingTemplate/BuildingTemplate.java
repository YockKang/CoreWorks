package com.main.CoreWorks.Factory.BuildingTemplate;

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.*;
import com.main.CoreWorks.Factory.Building;

public class BuildingTemplate {
    Class<? extends Building> clazz;
    JsonValue buildingData;

    public BuildingTemplate(JsonValue data) {
        String name = "com.main.CoreWorks.Factory." + data.getString("Type");

        try {
            Class<?> raw = ClassReflection.forName(name);
            if (!Building.class.isAssignableFrom(raw)) {
                throw new IllegalArgumentException(name + " is not a Building");
            }
            clazz = raw.asSubclass(Building.class);
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
        buildingData = data;
    };

    public Building of() {
        try {
            return clazz.getConstructor(JsonValue.class).newInstance(buildingData);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Template of: " + clazz;
    }

}
