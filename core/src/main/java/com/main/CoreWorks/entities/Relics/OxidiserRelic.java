package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class OxidiserRelic extends Relic {

    private static int DMG = 1;

    public OxidiserRelic() {
        super("OxidiserRelic",
            "Oxidiser",
            "Gain " + DMG + " damage permanently after each combat for normal attacks",
            "Gained " + DMG + " damage",
            "2");
    }

    @Override
    public void onCombatEnd(RunState runState) {
        runState.addPermBonus("BonusDmg", DMG);
    }

}
