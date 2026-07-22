package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class ReinforcedArmorRelic extends Relic {

    private static int SHIELD = 10;

    public ReinforcedArmorRelic() {
        super("ReinforcedArmorRelic",
            "Reinforced Armor",
            "Starts every combat with " + SHIELD + " shield",
            "Start every combat with " + SHIELD + " shield",
            "Reinforced Armor gave " + SHIELD + " shield",
            "1");
    }

    @Override
    public boolean onCombatStart(RunState runState) {
        runState.getPlayer().setShield(SHIELD);
        return true;
    }

}
