package com.main.CoreWorks.Generators;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.Factory.Upgrade.*;
import com.main.CoreWorks.Rewards.*;
import com.main.CoreWorks.RunPersistence.*;
import com.main.CoreWorks.database.*;
import com.main.CoreWorks.entities.Relics.Relic;

import java.util.Random;

public class RewardGenerator {
    public static Array<Reward> generateCombatReward(RunState runState) {
        Array<Reward> rewards = new Array<>();

        // Add the Rewards in the code below
        rewards.add(randomBuildingReward(runState));
        rewards.add(randomBuildingReward(runState));
        rewards.add(randomUpgradeReward(runState));
        rewards.add(randomUpgradeReward(runState));
        Reward relicReward = randomRelicReward(runState);
        if (relicReward != null) {
            rewards.add(relicReward);
        }
        rewards.add(randomExpansionReward(runState));

        // Return statement below
        return rewards;
    }

    public static Array<Reward> generateRestNodeReward(RunState runState) {
        Array<Reward> rewards = new Array<>();

        // Add the Rewards in the code below
        rewards.add(randomUpgradeReward(runState));
        rewards.add(randomUpgradeReward(runState));

        // Return statement below
        return rewards;
    }

    // Handles grid expansion reward generation
    // If grid rows > column, more likely to get column and vice versa
    private static Reward randomExpansionReward(RunState runState) {
        Random random = runState.getRandom();
        int odds = random.nextInt(100);
        FactoryGrid factoryGrid = runState.getFactoryGrid();

        if (factoryGrid.getMaxHeight() > factoryGrid.getMaxWidth()) {
            if (odds < 80) {
                return new ExpandGridColumnReward();
            } else {
                return new ExpandGridRowReward();
            }
        } else if (factoryGrid.getMaxHeight() < factoryGrid.getMaxWidth()) {
            if (odds < 20) {
                return new ExpandGridColumnReward();
            } else {
                return new ExpandGridRowReward();
            }
        } else {
            if (odds < 50) {
                return new ExpandGridColumnReward();
            } else {
                return new ExpandGridRowReward();
            }
        }
    }

    // Handles the building reward generation with the following odds:
    // Tier 0: 70%, Tier 1: 20%, Tier 2: 9%, Tier 3: 1%
    private static Reward randomBuildingReward(RunState runState) {
        Random random = runState.getRandom();
        double multiplier = Math.pow(runState.getCurrNode().getMultiplier(), 2);
        int num = random.nextInt(100);
        double t3Odds = (1 * multiplier);
        double t2Odds = (9 * multiplier);
        double t1Odds = (15 * multiplier);

        if (num <= t3Odds) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuildingId(runState.getRandom(), 3));
        } else if (num <= t3Odds + t2Odds) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuildingId(runState.getRandom(), 2));
        } else if (num <= t3Odds + t2Odds + t1Odds) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuildingId(runState.getRandom(), 1));
        } else {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuildingId(runState.getRandom(), 0));
        }
    }

    private static Reward randomUpgradeReward(RunState runState) {
        Random random = runState.getRandom();
        MapNode node = runState.getCurrNode();
        return new AddUpgradeReward(UpgradeFactory.randomUpgrade(random, node.getMultiplier()));
    }

    private static Reward randomRelicReward(RunState runState) {
        Random random = runState.getRandom();
        double multiplier = Math.pow(runState.getCurrNode().getMultiplier(), 2);
        int num = random.nextInt(100);
        double t3Odds = (1 * multiplier);
        double t2Odds = (9 * multiplier);
        double t1Odds = (15 * multiplier);

        int startTier = 0;
        int currTier = 0;
        boolean srcUp = true;

        if (num <= t3Odds) {
            startTier = 3;
            currTier = 3;
        } else if (num <= t3Odds + t2Odds) {
            startTier = 2;
            currTier = 2;
        } else if (num <= t3Odds + t2Odds + t1Odds) {
            startTier = 1;
            currTier = 1;
        }

        Relic relic = null;

        while (relic == null) {

            Array<Relic> thisTier = RelicGroupDatabase.getUnobtained(currTier, runState.getRelics());
            if (thisTier.size > 0) {
                relic = RelicGroupDatabase.getRandomRelic(currTier, random, runState.getRelics());
            } else {
                if (currTier >= 3) {
                    srcUp = false;
                    currTier = startTier;
                }
                if (srcUp) {
                    currTier++;
                } else {
                    currTier--;
                }
                if (currTier < 0) {
                    break;
                }
            }
        }

        if (relic != null) {
            return new AddRelicReward(relic);
        } else {
            return null;
        }



    }
}
