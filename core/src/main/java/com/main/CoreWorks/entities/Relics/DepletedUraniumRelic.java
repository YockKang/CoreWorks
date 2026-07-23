package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class DepletedUraniumRelic extends Relic {

    private static int DMG = 2;

    public DepletedUraniumRelic(){
        super("DepletedUraniumRelic",
            "Depleted Uranium",
            "Deal additional " + DMG + " true damage on each attack",
            "Deal additional " + DMG + " true damage on each attack",
            "You coated Depleted Uranium on your shooters",
            "2");
    }

    @Override
    public boolean onCombatStart(RunState runState) {
        runState.addTempBonus("ExtraAtkTrue", DMG);
        return true;
    }
}

