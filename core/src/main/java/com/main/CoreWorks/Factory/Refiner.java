package com.main.CoreWorks.Factory;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.*;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.moveset.*;
import com.main.CoreWorks.util.MathExtras;

import java.util.Objects;

public class Refiner extends Building{

    boolean isCrafting = false;

    ObjectMap<String, Modifier> productMods = new ObjectMap<>();

    public Refiner(int coolDown, boolean[][] shape, int mineMult, String name) {
        super(coolDown,
            new Array<>(0),
            new Array<>(0),
            shape,
            "refiner");
    }

    public Refiner(int coolDown, boolean[][] shape, int mineMult, String name, Recipe rec) {
        super(coolDown,
            new Array<>(0),
            new Array<>(0),
            shape,
            "refiner");
        this.recipe = rec;
        setRecipe(rec);
    }

    public Refiner(JsonValue data) {
        super(data);
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name).append(" #").append(idNum)
            .append("\nOnGrid: ").append(onGrid)
            .append("\nSpeedMult ")
            .append(speedMultiplier)
            .append("\nInput Buffer ")
            .append(inputBuffer)
            .append("\nCrafting\n")
            .append(recipe)
            .append("\nCrafting?: ")
            .append(isCrafting).append(", ").append(currCooldown).append("/").append(cooldownTimer)
            .append("\nOutput Buffer ")
            .append(outputBuffer)
            .toString();
    }

    @Override
    public Array<Move> updateEnabled(RunState runState) {
        if (!isCrafting) {
            if (tryStartCraft()) {
                isCrafting = true;
                startCraft();
                status = Status.WORKING;
            } else {
                status = Status.NO_INPUT;
            }
        } else {
            currCooldown += getSpeed();
            if (currCooldown >= cooldownTimer) {
                boolean craftSuccess = tryEndCraft();
                if (craftSuccess) {
                    endCraft();
                    currCooldown -= cooldownTimer;
                    if (!tryStartCraft()) {
                        isCrafting = false;
                        currCooldown = 0;
                        status = Status.NO_INPUT;
                    } else {
                        startCraft();
                        status = Status.WORKING;
                    }
                } else {
                    currCooldown = cooldownTimer - getSpeed();
                    status = Status.FULL_OUTPUT;
                }
            }
        }
        return null;
    }

    public boolean tryStartCraft() {
        if (this.recipe == null) {
            return false;
        } else {
            Array<Integer> mults = this.recipe.getInputMultipliers();
            // check if enough to start crafting
            for (int i = 0; i < mults.size; i++) {
                if (!this.inputBuffer.get(i).tryDraw(mults.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public void startCraft() {
        productMods.clear();
        Array<Integer> mults = this.recipe.getInputMultipliers();
        Array<ObjectMap<String, Modifier>> inputModifiers = new Array<>();
        ObjectMap<String, Array<Modifier>> craftModifiers = new ObjectMap<>();
        for (int i = 0; i < mults.size; i++) {
            Array<Resource> consumed = this.inputBuffer.get(i).draw(mults.get(i));
            for (Resource rsc : consumed) {
                inputModifiers.add(rsc.getModifiers());
            }
        }
        for (ObjectMap<String, Modifier> mods : inputModifiers) {
            for (ObjectMap.Entry<String, Modifier> entry : mods) {
                if (!craftModifiers.containsKey(entry.key)) {
                    craftModifiers.put(entry.key, new Array<>());
                }
                craftModifiers.get(entry.key).add(entry.value);
            }
        }
        for (ObjectMap.Entry<String, Array<Modifier>> entry : craftModifiers) {
            float avgVal = 0;
            String newStrVal = entry.value.first().getStrValue();
            if (newStrVal == null) {
                float sum = 0;
                for (Modifier mod : entry.value) {
                    sum += mod.getValue();
                }
                avgVal = sum / entry.value.size;
            } else {
                for (Modifier mod : entry.value) {
                    if (!Objects.equals(newStrVal, mod.getStrValue())) {
                        newStrVal = "";
                        break;
                    }
                }
            }
            productMods.put(entry.key, new Modifier(entry.key, avgVal, newStrVal));
        }
        productMods.putAll(resourceModifiers);
    }

    public boolean tryEndCraft() {
        System.out.println("try end");
        if (this.recipe == null) {
            return false;
        } else {
            Array<Integer> mults = this.recipe.getOutputMultipliers();
            for (int i = 0; i < mults.size; i++) {
                if (!this.outputBuffer.get(i).tryAdd(mults.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public void endCraft() {
        Array<Integer> mults = this.recipe.getOutputMultipliers();
        for (int i = 0; i < mults.size; i++) {
            this.outputBuffer.get(i).addNew(mults.get(i), productMods);
        }
        productMods.clear();
    }

    @Override
    public void clear() {
        super.clear();
        isCrafting = false;
    }

    @Override
    public Table tooltipDisplay(Skin skin) {
        Table displayTable = new Table(skin);
        displayTable.setBackground("default-round");
        displayTable.add(new Label(name, skin)).row();
        if (this.recipe != null) {
            displayTable.add(new Label("Crafting: " + this.recipe.getName(), skin)).row();
        } else {
            Label errMsg = new Label("Recipe not set!", skin);
            errMsg.setColor(1,0,0,1);
            displayTable.add(errMsg).row();
        }
        displayTable.add(new Label("Speed: " + MathExtras.roundDP(getSpeed(), 2), skin)).row();
        return displayTable;
    }
}
