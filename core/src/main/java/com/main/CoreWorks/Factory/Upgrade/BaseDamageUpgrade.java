package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Shooter;
import com.main.CoreWorks.util.MathExtras;

public class BaseDamageUpgrade extends UpgradeAspect{
    public BaseDamageUpgrade(float value) {
        super(value, "Base Damage +");
    }

    @Override
    public void execute(Building b) {
        if (b instanceof Shooter) {
            ((Shooter) b).changeBaseDamage(value);
        }
    }

    @Override
    public boolean tryExecute(Building b) {
        if (b instanceof Shooter) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String changes(Building b) {
        StringBuilder str = new StringBuilder().append("Base Damage: ");
        if (b instanceof Shooter shooter) {
            str.append( MathExtras.roundDP(shooter.getBaseDmg(), 2) )
                .append(" -> ")
                .append( MathExtras.roundDP(shooter.getBaseDmg() + value, 2) );
        } else {
            str.append("Not Applicable");
        }
        return str.toString();
    }
}
