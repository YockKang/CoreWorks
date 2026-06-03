package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.*;

public class RecipeDatabase {
    private static final ObjectMap<String, Recipe> RecipeDB = new ObjectMap<>();

    public static Recipe register(Array<Resource> inputs,
                                  Array<Resource> outputs,
                                  Array<Integer> inputMultiple,
                                  Array<Integer> outputMultiple,
                                  Array<String> groups,
                                  int dur,
                                  String name,
                                  String id) {
        Recipe type = new Recipe(inputs,
            outputs,
            inputMultiple,
            outputMultiple,
            groups,
            dur,
            name,
            id);
        RecipeDB.put(id, type);
        return type;
    }

    public static Recipe register(Resource[] inputs,
                                  Resource[] outputs,
                                  Integer[] inputMultiple,
                                  Integer[] outputMultiple,
                                  String[] groups,
                                  int dur,
                                  String name,
                                  String id) {
        Recipe type = new Recipe(inputs,
            outputs,
            inputMultiple,
            outputMultiple,
            groups,
            dur,
            name,
            id);
        RecipeDB.put(id, type);
        return type;
    }

    public static Recipe register(JsonValue data) {
        if (data.isArray()){
            data.forEach(RecipeDatabase::register);
            return null;
        } else {
            Recipe type = new Recipe(data);
            RecipeDB.put(data.getString("id"), type);
            return type;
        }
    }

    public static Recipe get(String id) {
        return RecipeDB.get(id);
    }


    public static String showDB(){
        return RecipeDB.toString();
    }

    protected static ObjectMap<String, Recipe> getDB() {
        return RecipeDB;
    }
}
