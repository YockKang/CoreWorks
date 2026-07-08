package com.main.CoreWorks.simulators;


import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.Relics.Relic;

public class CombatController {
    private FactorySim factorySim;
    private CombatSim combatSim;

    public CombatController(FactorySim factorySim, CombatSim combatSim) {
        this.factorySim = factorySim;
        this.combatSim = combatSim;
        combatSim.setGrid(factorySim.getGrid());
    }

    public void advanceTick(RunState runState, int tick) {
        if (combatSim.isWin() || combatSim.isLost()) {
            factorySim.clear();
            // When win/loss is resolved, trigger all relic on-end effects
            for (Relic relic : runState.getRelics()) {
                relic.onCombatEnd(runState);
            }
            return;
        }

        // On start (tick 0), trigger all on-start relic effects
        if (tick == 0) {
            for (Relic relic : runState.getRelics()) {
                boolean happened = relic.onCombatStart(runState);
                if (happened) {
                    combatSim.addLog(tick, relic.getLog());
                }
            }
        }

        // Firstly, tick the factory
        factorySim.advanceTick(runState);

        // Then, transfer the factory actions to combat
        combatSim.enqueueMoves(factorySim.returnMoves());

        // Lastly, resolve combat (All on tick relic effects are handled here)
        combatSim.advanceTick(runState, tick);
    }

    public boolean isWin() {
        return combatSim.isWin();
    }

    public boolean isLost() {
        return combatSim.isLost();
    }

    public CombatSim getCombatSim() {
        return combatSim;
    }

    public FactorySim getFactorySim() {
        return factorySim;
    }
}
