package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class MoneyRelic2 extends Relic {

    private static int MONEY = 40;

    public MoneyRelic2() {
        super("MoneyRelic2",
            "Money Pot",
            "Gives " + MONEY + " money on acquisition",
            "Gives " + MONEY + " money on acquisition",
            "Money Pot gave one-time grant of " + MONEY + " coins",
            "1");
    }

    @Override
    public void onAcquire(RunState runState) { runState.getPlayer().gainMoney(MONEY); };

}
