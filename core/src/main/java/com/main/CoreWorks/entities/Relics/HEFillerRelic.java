package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class HEFillerRelic extends Relic {

    private static int DMG = 3;

    public HEFillerRelic() {
        super("HEFillerRelic",
            "High-explosive filler",
            "Deal additional " + DMG + " damage on normal attacks",
            "Added HE filler into the shooters",
            "1");
    }

    @Override
    public boolean onCombatStart(RunState runState) {
        runState.addTempPlayerBonusDmg(DMG);
        return true;
    }

}
