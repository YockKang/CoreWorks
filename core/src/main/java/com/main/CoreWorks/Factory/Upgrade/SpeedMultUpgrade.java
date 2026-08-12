package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.util.MathExtras;

public class SpeedMultUpgrade  extends UpgradeAspect {
    public SpeedMultUpgrade(float value) {
        super(value, "Speed Multiplier +");
    }

    @Override
    public void execute(Building b) {
        b.addSpeedMult(value);
    }

    @Override
    public boolean tryExecute(Building b) {
        return true;
    }

    @Override
    public String changes(Building b) {
        return new StringBuilder()
            .append("Speed Multiplier: ")
            .append( MathExtras.roundDP( b.getSpeedMult(), 2 ) )
            .append(" -> ")
            .append( MathExtras.roundDP(b.getSpeedMult() + value, 2 ) )
            .toString();
    }
}
