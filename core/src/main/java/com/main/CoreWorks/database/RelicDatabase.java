package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.entities.Relics.Relic;

public class RelicDatabase {
    private static final ObjectMap<String, Relic> RelicDB = new ObjectMap<>(3);

    public static void register(JsonValue data) {

        String[] relicNames = data.asStringArray();
        for (String relicName : relicNames) {
            String name = "com.main.CoreWorks.entities.Relics." + relicName;
            Class<? extends Relic> clazz = null;
            try {
                Class<?> raw = ClassReflection.forName(name);
                if (!Relic.class.isAssignableFrom(raw)) {
                    throw new IllegalArgumentException(name + " is not a Relic");
                }
                clazz = raw.asSubclass(Relic.class);
            } catch (ReflectionException e) {
                throw new RuntimeException(e);
            }
            Relic relic = null;
            try {
                relic = clazz.getConstructor().newInstance();
            } catch (Exception e) {
                System.out.println("Relic Generation Error");
            }
            if (relic != null) {
                RelicDB.put(relic.getId(), relic);
            }
        }

    }

    public static Relic get(String id) {
        return RelicDB.get(id);
    }

    public static String showDB(){
        return RelicDB.toString();
    }

    public static ObjectMap<String, Relic> getDB(){
        return RelicDB;
    }
}
