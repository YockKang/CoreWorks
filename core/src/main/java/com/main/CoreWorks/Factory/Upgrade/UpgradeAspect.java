package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.Building;

public abstract class UpgradeAspect {
    protected float value;

    public UpgradeAspect(float val) {
        value = val;
    }

    public abstract void execute(Building b);

    public abstract boolean tryExecute(Building b);
}
