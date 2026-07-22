package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class PhoenixCoreRelic extends Relic {

    private static int USES = 1;

    public PhoenixCoreRelic() {
        super("PhoenixCoreRelic",
            "Phoenix's Core",
            "Prevents death and restores all HP, Uses remaining: " + USES,
            "Prevents death and restores all HP " + USES + " time",
            "Phoenix's Core prevented your demise!",
            "3");
    }

    @Override
    public boolean onTick(RunState runState) {
        if (runState.getPlayer().isDead() && USES != 0) {
            // First, set the HP to 0 to remove any overkill HP loss
            runState.getPlayer().heal(-(runState.getPlayer().displayCurrentHp()));
            // Then, heal to full HP
            runState.getPlayer().heal(runState.getPlayer().displayMaxHp());
            USES--;
            return true;
        }
        return false;
    }
}
