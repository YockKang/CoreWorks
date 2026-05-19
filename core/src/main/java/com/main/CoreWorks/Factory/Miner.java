package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.Move;

public class Miner extends Building {

    protected int mineMultiplier;

    public Miner(int coolDown, int outBufferSize, boolean[][][] shape, int mineMult) {
        super(coolDown,
            new Array<Integer>(0),
            new Array<Integer>(0),
            new Array<Integer>(0),
            new Array<Integer>(0),
            shape,
            "miner");
        this.mineMultiplier = mineMult;
    }

    public Miner(int coolDown, Array<Integer> outBufferSize, boolean[][][] shape, int mineMult, Recipe rec) {
        super(coolDown,
            new Array<Integer>(0),
            outBufferSize,
            new Array<Integer>(0),
            new Array<Integer>(0),
            shape,
            "miner");
        this.mineMultiplier = mineMult;
        this.recipe = rec;
        setRecipe(rec);
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name)
            .append(" ")
            .append(id)
            .append('\n')
            .append(recipe)
            .append("\nOutput Capacity ")
            .append(outputBufferSize)
            .append("\nOutput Buffer ")
            .append(outputBuffer)
            .toString();
    }

    @Override
    public Move updateTick() {
        if (currCooldown >= cooldownTimer) {
            boolean mineSuccess = mine();
            if (mineSuccess) {
                currCooldown = 0;
            }
        } else {
            currCooldown++;
        }
        return null;
    }

    public void setRecipe(Recipe rec) {
        // write new recipe
        this.recipe = rec;
        // grab new outputs
        Array<Resource> Outputs = this.recipe.getOutputs();
        // reset queues
        this.outputBuffer.clear();
        for (Resource item : Outputs) {
            this.outputBuffer.add(0);
        }
        this.outputBuffer.shrink();
    }

    public boolean mine() {
        Array<Integer> mults = this.recipe.getOutputMultipliers();
        // attempt to extract all into buffers
        for (int i = 0; i < mults.size; i++) {
            int expected = this.outputBuffer.get(i) + mults.get(i) * this.mineMultiplier;
            // buffer for that type is full, cannot mine
            if (expected > this.outputBufferSize.get(i)) {
                return false;
            }
        }
        for (int i = 0; i < mults.size; i++) {
            this.outputBuffer.set(i,
                this.outputBuffer.get(i) + mults.get(i) * this.mineMultiplier);
        }
        return true;
    }
}
