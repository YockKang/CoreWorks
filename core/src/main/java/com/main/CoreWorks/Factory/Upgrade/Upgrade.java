package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.*;

public class Upgrade {
    private Array<UpgradeAspect> upgrades;

    public Upgrade() {

    }

    public boolean anyFail(Building b) {
        for (UpgradeAspect ua : upgrades) {
            if (!ua.tryExecute(b)) {
                return true;
            }
        }
        return false;
    }

    public boolean allFail(Building b) {
        for (UpgradeAspect ua : upgrades) {
            if (ua.tryExecute(b)) {
                return false;
            }
        }
        return true;
    }

    public void execute(Building b) {
        upgrades.forEach(ua -> ua.execute(b));
    }
}
