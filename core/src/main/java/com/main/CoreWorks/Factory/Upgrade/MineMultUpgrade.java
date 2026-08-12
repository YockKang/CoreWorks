package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.*;

public class MineMultUpgrade extends UpgradeAspect{
    public MineMultUpgrade(float value) {
        super(value, "Mine Multiplier +");
    }

    @Override
    public void execute(Building b) {
        if (b instanceof Miner) {
            ((Miner) b).addMineMult(value);
        }
    }

    @Override
    public boolean tryExecute(Building b) {
        if (b instanceof Miner) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String changes(Building b) {
        StringBuilder str = new StringBuilder().append("Mine Multiplier: ");
        if (b instanceof Miner miner) {
            str.append( (int) miner.getMineMultiplier() )
                .append(" -> ")
                .append( (int) (miner.getMineMultiplier() + value) );
        } else {
            str.append("Not Applicable");
        }
        return str.toString();
    }
}
