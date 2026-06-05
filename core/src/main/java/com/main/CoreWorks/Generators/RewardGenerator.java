package com.main.CoreWorks.Generators;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Rewards.AddBuildingReward;
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.database.BuildingTierDatabase;

public class RewardGenerator {
    public static Array<Reward> generateReward(RunState runState) {
        Array<Reward> rewards = new Array<>();

        // Add the Rewards in the code below
        rewards.add(randomBuildingReward(runState));

        // Return statement below
        return rewards;
    }

    // Handles the building reward generation with the following odds:
    // Tier 0: 70%, Tier 1: 20%, Tier 2: 9%, Tier 3: 1%
    private static Reward randomBuildingReward(RunState runState) {
        int num = runState.getRandom().nextInt(100);

        if (num <= 70) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuilding(runState.getRandom(), 0));
        } else if (num <= 90) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuilding(runState.getRandom(), 1));
        } else if (num <= 98) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuilding(runState.getRandom(), 2));
        } else {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuilding(runState.getRandom(), 3));
        }
    }
}
