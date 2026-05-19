package com.main.CoreWorks.database;


import com.main.CoreWorks.Factory.Building;

import java.util.HashMap;
import java.util.Map;

public class BuildingDatabase {

    private static final Map<String, Building> BuildingDB = new HashMap<>();

    public static void register(Building b) {
        String id = b.toString();
        BuildingDB.put(id, b);
    }

    public static Building get(String id) {
        return BuildingDB.get(id);
    }

}
