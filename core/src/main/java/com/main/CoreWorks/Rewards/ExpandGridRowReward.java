package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.RunPersistence.RunState;

public class ExpandGridRowReward extends Reward {
    public ExpandGridRowReward() {
        super("Expand grid row", "Expands grid by 1 row");
    }

    @Override
    public boolean needTarget() {
        return false;
    }

    @Override
    public void apply(RunState runState) {
        FactoryGrid factoryGrid = runState.getFactoryGrid();
        int currentRows = factoryGrid.getMaxHeight();
        factoryGrid.changeSize(currentRows + 1, factoryGrid.getMaxWidth());
    }
}
