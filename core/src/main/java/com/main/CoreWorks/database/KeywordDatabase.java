package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.main.CoreWorks.TextParser.Keyword;

public class KeywordDatabase {
    private static final ObjectMap<String, Keyword> KeywordDB = new ObjectMap<>();
    public static Keyword register(JsonValue data) {
        if (data.isArray()) {
            data.forEach(KeywordDatabase::register);
            return null;
        } else {
            String id = data.getString("Word").toLowerCase();
            Keyword kw = new Keyword(data);
            KeywordDB.put(id, kw);
            return kw;
        }
    }

    public static Keyword getKeyword(String word) {
        return KeywordDB.get(word.toLowerCase());
    }

    public static Array<String> getKeywords() {return KeywordDB.keys().toArray(); }

    public static boolean hasWord(String word) {
        return KeywordDB.containsKey(word.toLowerCase());
    }

    public static String showDB(){
        return KeywordDB.toString();
    }
}
