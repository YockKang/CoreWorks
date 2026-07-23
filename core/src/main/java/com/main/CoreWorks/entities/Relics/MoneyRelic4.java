package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class MoneyRelic4 extends Relic {

    private static int MONEY = 400;

    public MoneyRelic4() {
        super("MoneyRelic4",
            "Money Bank",
            "Gives " + MONEY + " money on acquisition",
            "Gives " + MONEY + " money on acquisition",
            "Money Bank gave one-time grant of " + MONEY + " coins",
            "3");
    }

    @Override
    public void onAcquire(RunState runState) { runState.getPlayer().gainMoney(MONEY); };

}
