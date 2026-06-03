package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;

public class RecipeGroupDatabase {
    private static final ObjectMap<String, Array<Recipe>> GroupDB = new ObjectMap<>();

    public static void update() {
        GroupDB.clear();
        RecipeDatabase.getDB()
            .forEach(rc ->
                rc.value.getGroups().forEach(grp -> {
                    if (!GroupDB.containsKey(grp)) {
                        GroupDB.put(grp, new Array<>(1));
                    }
                    GroupDB.get(grp).add(rc.value);
                })
            );
    }

    public static Array<Recipe> get(String id) {
        return GroupDB.get(id);
    }


    public static String showDB(){
        return GroupDB.toString();
    }
}
