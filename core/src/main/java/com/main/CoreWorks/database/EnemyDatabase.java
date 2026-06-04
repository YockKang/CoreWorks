package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.entities.*;
import com.main.CoreWorks.moveset.*;

public class EnemyDatabase {

    private static final ObjectMap<String, EnemyFactory> EnemyDB = new ObjectMap<>();

    public static Enemy register(JsonValue data) {
        if (data.isArray()) {
            data.forEach(com.main.CoreWorks.database.EnemyDatabase::register);
            return null;
        } else {
            EnemyFactory tp = new EnemyFactory(data);
            String id = data.getString("id");
            EnemyDB.put(id, tp);
            return tp.of(1);
        }
    }

    public static EnemyFactory getEnemyConstructor(String id) {
        return EnemyDB.get(id);
    }

    public static Enemy getEnemy(String id, float multiplier) {
        return EnemyDB.get(id).of(multiplier);
    }

    public static String showDB(){
        return EnemyDB.toString();
    }

}
