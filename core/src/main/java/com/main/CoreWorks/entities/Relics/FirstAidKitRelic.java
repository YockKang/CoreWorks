package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class FirstAidKitRelic extends Relic {

    private static int HEAL = 10;

    public FirstAidKitRelic() {
        super("FirstAidKitRelic",
            "First Aid Kit",
            "Heals " + HEAL + " HP after every combat win",
            "Heals " + HEAL + " HP after every combat win",
            "First Aid Kit healed for " + HEAL,
            "0");
    }

    @Override
    public void onCombatEnd(RunState runState) {
        runState.getPlayer().heal(HEAL);
    }

}
