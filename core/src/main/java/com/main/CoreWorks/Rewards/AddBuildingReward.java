package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.database.BuildingDatabase;

public class AddBuildingReward extends Reward {
    private String buildingId;

    public AddBuildingReward(String buildingId) {
        super("Add a building", String.format("Adds 1 %s to your inventory", BuildingDatabase.getBuildingConstructor(buildingId).getName()));
        this.buildingId = buildingId;
    }

    @Override
    public boolean needTarget() {
        return false;
    }

    @Override
    public void apply(RunState runState) {
        runState.getPlayer().addBuilding(BuildingDatabase.getBuilding(buildingId));
    }
}
