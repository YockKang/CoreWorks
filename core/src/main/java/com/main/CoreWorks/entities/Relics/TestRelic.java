package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class TestRelic extends Relic {

    private static int DMG = 2;

    public TestRelic(){
        super("TestRelic",
            "Test",
            "Deal additional " + DMG + " true damage on each attack",
            "Test Relic Procced",
            "1");
    }

    @Override
    public boolean onCombatStart(RunState runState) {
        runState.addTempBonus("ExtraAtkTrue", DMG);
        return true;
    }
}

