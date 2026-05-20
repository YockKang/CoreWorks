package com.main.CoreWorks.database;


import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Miner;


public class BuildingDatabase {

    public static Building createMiner() {
        return new Miner(4, new boolean[][] {{true}}, 1, null);
    }


}
