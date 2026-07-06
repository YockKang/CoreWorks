package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.entities.EnemyFactory;

public class KeywordDatabase {
    private static final ObjectMap<String, String> KeywordDB = new ObjectMap<>();

    public static String register(JsonValue data) {
        if (data.isArray()) {
            data.forEach(KeywordDatabase::register);
            return null;
        } else {
            String id = data.getString("Word");
            String desc = data.getString("Description");
            KeywordDB.put(id, desc);
            return desc;
        }
    }

    public static String getDescription(String id) {
        return KeywordDB.get(id.toLowerCase());
    }

    public static boolean hasWord(String id) {
        return KeywordDB.containsKey(id.toLowerCase());
    }

    public static String showDB(){
        return KeywordDB.toString();
    }
}
