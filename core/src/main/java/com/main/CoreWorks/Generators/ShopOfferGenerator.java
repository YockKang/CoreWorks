package com.main.CoreWorks.Generators;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.Rewards.*;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.database.BuildingTierDatabase;
import com.main.CoreWorks.database.RelicGroupDatabase;
import com.main.CoreWorks.entities.Relics.Relic;

import java.util.Random;
import java.util.function.IntFunction;

public class ShopOfferGenerator {
    public static Array<ShopOffer> generateShopOffers(RunState runState) {
        Array<ShopOffer> offers = new Array<>();
        Array<Relic> offeredRelics = new Array<>();

        // Add the shop offers here
        offers.add(randomBuildingOffer(runState));
        offers.add(randomBuildingOffer(runState));
        ShopOffer relicOffer1 = randomRelicOffer(runState, offeredRelics);
        if (relicOffer1 != null) {
            offers.add(relicOffer1);
        }
        ShopOffer relicOffer2 = randomRelicOffer(runState, offeredRelics);
        if (relicOffer2 != null) {
            offers.add(relicOffer2);
        }
        offers.add(RowExpansionOffer(runState));
        offers.add(ColExpansionOffer(runState));

        return offers;
    }

    private static ShopOffer RowExpansionOffer(RunState runState) {
        FactoryGrid factoryGrid = runState.getFactoryGrid();
        // Set base cost
        int cost = 400;
        int scaling = 0;

        // If row > col, make it more expensive
        if (factoryGrid.getMaxHeight() > factoryGrid.getMaxWidth()) {
            scaling = (factoryGrid.getMaxHeight() - factoryGrid.getMaxWidth()) * 100;
        }
        return new ShopOffer(cost + scaling, new ExpandGridRowReward());
    }

    private static ShopOffer ColExpansionOffer(RunState runState) {
        FactoryGrid factoryGrid = runState.getFactoryGrid();
        // Set base cost
        int cost = 400;
        int scaling = 0;

        // If row < col, make it more expensive
        if (factoryGrid.getMaxHeight() < factoryGrid.getMaxWidth()) {
            scaling = (factoryGrid.getMaxWidth() - factoryGrid.getMaxHeight()) * 100;
        }
        return new ShopOffer(cost + scaling, new ExpandGridColumnReward());
    }

    private static ShopOffer randomBuildingOffer(RunState runState) {
        Random random = runState.getRandom();
        double multiplier = Math.pow(runState.getCurrNode().getMultiplier(), 2);
        int num = random.nextInt(100);
        int t0Cost = 50;
        int t1Cost = 100;
        int t2Cost = 200;
        int t3Cost = 400;
        double t3Odds = (1 * multiplier);
        double t2Odds = (9 * multiplier);
        double t1Odds = (15 * multiplier);

        if (num <= t3Odds) {
            return new ShopOffer(t3Cost, new AddBuildingReward(BuildingTierDatabase.getRandomBuildingId(runState.getRandom(), 3)));
        } else if (num <= t3Odds + t2Odds) {
            return new ShopOffer(t2Cost, new AddBuildingReward(BuildingTierDatabase.getRandomBuildingId(runState.getRandom(), 2)));
        } else if (num <= t3Odds + t2Odds + t1Odds) {
            return new ShopOffer(t1Cost, new AddBuildingReward(BuildingTierDatabase.getRandomBuildingId(runState.getRandom(), 1)));
        } else {
            return new ShopOffer(t0Cost, new AddBuildingReward(BuildingTierDatabase.getRandomBuildingId(runState.getRandom(), 0)));
        }
    }

    private static ShopOffer randomRelicOffer(RunState runState, Array<Relic> offeredRelics) {
        Random random = runState.getRandom();
        double multiplier = Math.pow(runState.getCurrNode().getMultiplier(), 2);
        int num = random.nextInt(100);
        // Use switch case to determine the price at each tier
        IntFunction<Integer> tieredCost = tier -> switch (tier) {
            case 0 -> 50;
            case 1 -> 100;
            case 2 -> 200;
            case 3 -> 400;
            default -> 800;
        };
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

            // Remove already existing relics
            Array<Relic> filtered = new Array<>();
            for (Relic relic1 : thisTier) {
                if (!offeredRelics.contains(relic1, true)) {
                    filtered.add(relic1);
                }
            }

            if (filtered.size > 0) {
                relic = filtered.random();
                offeredRelics.add(relic);
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
            int baseCost = tieredCost.apply(currTier);
            return new ShopOffer(baseCost, new AddRelicReward(relic));
        } else {
            return null;
        }
    }
}
