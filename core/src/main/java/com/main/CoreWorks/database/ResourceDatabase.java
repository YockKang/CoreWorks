package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.*;

public class ResourceDatabase {
    private static final ObjectMap<String, ResourceTemplate> ResourceDB = new ObjectMap<>();

    public static Resource register(JsonValue data) {
        if (data.isArray()){
            data.forEach(ResourceDatabase::register);
            return null;
        } else {
            ResourceTemplate type = new ResourceTemplate(data);
            ResourceDB.put(data.getString("id"), type);
            return type.of();
        }
    }

    public static Resource get(String id) {
        return ResourceDB.get(id).of();
    }

    public static Resource get(String id, ObjectMap<String, Modifier> mods) {
        return ResourceDB.get(id).of(mods);
    }

    public static String getName(String id) {
        return ResourceDB.get(id).getName();
    }

    public static String showDB(){
        return ResourceDB.toString();
    }

    public static ObjectMap<String, ResourceTemplate> getDB() {
        return ResourceDB;
    }
}
