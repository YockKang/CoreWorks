package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class MoneyRelic extends Relic {


    public MoneyRelic() {
        super("MoneyRelic",
            "Money",
            "Gives 50 Coin",
            "0");
    }

    // Called when relic has on-collection effects
    public void onAcquire(RunState runState) { runState.getPlayer().gainMoney(50); };

}
