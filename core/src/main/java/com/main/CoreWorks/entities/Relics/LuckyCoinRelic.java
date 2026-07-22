package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class LuckyCoinRelic extends Relic {

    private static int MONEY = 10;

    public LuckyCoinRelic() {
        super("LuckyCoinRelic",
            "Lucky Coin",
            "Gives " + MONEY + " money after every combat win",
            "Gives " + MONEY + " money after every combat win",
            "Lucky Coin spawned " + MONEY + " coins",
            "0");
    }

    @Override
    public void onCombatEnd(RunState runState) {
        runState.getPlayer().gainMoney(MONEY);
    }

}
