package com.main.CoreWorks.Factory.BuildingTemplate;

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.*;
import com.main.CoreWorks.Codex.BuildingEntry;
import com.main.CoreWorks.Codex.Entry;
import com.main.CoreWorks.Factory.Building;

public class BuildingTemplate {
    protected Class<? extends Building> clazz;
    protected JsonValue buildingData;
    protected int idNum = 0;

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
            JsonValue id = new JsonValue(idNum);
            buildingData.setChild("idNum", id);
            Building bldg = clazz.getConstructor(JsonValue.class).newInstance(buildingData);
            idNum++;
            return bldg;
        } catch (Exception e) {
            System.out.println("Building Generation Error");
            return null;
        }
    }

    public BuildingEntry generateCodexEntry() {
        try {
            Class<? extends BuildingEntry> clazz;
            String name = "com.main.CoreWorks.Codex." + buildingData.getString("Type") + "Entry";

            try {
                Class<?> raw = ClassReflection.forName(name);
                if (!BuildingEntry.class.isAssignableFrom(raw)) {
                    throw new IllegalArgumentException(name + " is not a codex entry type");
                }
                clazz = raw.asSubclass(BuildingEntry.class);
            } catch (ReflectionException e) {
                throw new RuntimeException(e);
            }
            return clazz.getConstructor(JsonValue.class).newInstance(buildingData);
        } catch (Exception e) {
            System.out.println("Entry Generation Error");
            return null;
        }
    }

    @Override
    public String toString() {
        return "Template of: " + clazz;
    }

    public String getName() { return buildingData.getString("Name"); }
}
