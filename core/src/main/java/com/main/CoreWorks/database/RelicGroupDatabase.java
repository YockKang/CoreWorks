package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.entities.EnemyFactory;
import com.main.CoreWorks.entities.Relics.Relic;

import java.util.Random;

public class RelicGroupDatabase {
    private static final ObjectMap<String, Array<Relic>> RelicGroupDB = new ObjectMap<>(3);

    public static void register() {
        ObjectMap<String, Relic> relics = RelicDatabase.getDB();
        for (ObjectMap.Entry<String, Relic> e : relics) {
            Relic r = e.value;
            String tier = r.getTier();
            if (RelicGroupDB.get(tier) == null) {
                RelicGroupDB.put(tier, new Array<>());
            }
            RelicGroupDB.get(tier).add(r);
        }
    }

    public static Array<Relic> getTier(String tier) {
        return RelicGroupDB.get(tier);
    }

    public static Array<Relic> getTier(int tier) {
        return getTier(String.valueOf(tier));
    }

    public static Relic getRelic(String tier, int num) {
        return RelicGroupDB.get(tier).get(num);
    }

    public static Relic getRelic(int tier, int num) {
        return getRelic(String.valueOf(tier), num);
    }

    public static Relic getRandomRelic(String tier, Random random) {
        return RelicGroupDB.get(tier).get(random.nextInt(RelicGroupDB.get(tier).size));
    }

    public static Relic getRandomRelic(int tier, Random random) {
        return getRandomRelic(String.valueOf(tier), random);
    }

    public static Relic getRandomRelic(String tier, Random random, Array<Relic> relics) {
        Array<Relic> tierRelics = new Array<>(RelicGroupDB.get(tier));
        tierRelics.removeAll(relics, false);
        return tierRelics.get(random.nextInt(tierRelics.size));
    }

    public static Relic getRandomRelic(int tier, Random random, Array<Relic> relics) {
        return getRandomRelic(String.valueOf(tier), random, relics);
    }

    public static Array<Relic> getUnobtained(String tier, Array<Relic> relics) {
        Array<Relic> tierRelics = new Array<>(RelicGroupDB.get(tier));
        tierRelics.removeAll(relics, false);
        return tierRelics;
    }

    public static Array<Relic> getUnobtained(int tier, Array<Relic> relics) {
        return getUnobtained(String.valueOf(tier), relics);
    }

    public static String showDB(){
        return RelicGroupDB.toString();
    }
}

