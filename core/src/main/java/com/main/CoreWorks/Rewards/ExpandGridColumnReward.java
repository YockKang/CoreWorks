package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.RunPersistence.RunState;

public class ExpandGridColumnReward extends Reward {
    public ExpandGridColumnReward() {
        super("Expand grid column", "Expands grid by 1 column");
    }

    @Override
    public boolean needTarget() {
        return false;
    }

    @Override
    public void apply(RunState runState) {
        FactoryGrid factoryGrid = runState.getFactoryGrid();
        int currentCols = factoryGrid.getMaxWidth();
        factoryGrid.changeSize(factoryGrid.getMaxHeight(), currentCols + 1);
    }
}
