package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.BuildingTemplate.*;

public class BuildingDatabase {
    private static final ObjectMap<String, BuildingTemplate> BuildingDB = new ObjectMap<>();

    public static Building register(JsonValue data) {
        if (data.isArray()) {
            data.forEach(BuildingDatabase::register);
            return null;
        } else {
            BuildingTemplate tp = new BuildingTemplate(data);
            String id = data.getString("id");
            BuildingDB.put(id, tp);
            return tp.of();
        }
    }

    public static BuildingTemplate getBuildingConstructor(String id) {
        return BuildingDB.get(id);
    }

    public static Building getBuilding(String id) {
        return BuildingDB.get(id).of();
    }

    public static String showDB(){
        return BuildingDB.toString();
    }

}
