package com.main.CoreWorks.moveset;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.entities.Character;
import com.main.CoreWorks.entities.Enemy;

public abstract class Move {
    protected String name;
    protected String description;
    protected int chargeTime;
    protected Character target;
    protected boolean hitAll;

    public Move(String name, String description, int chargeTime) {
        this.name = name;
        this.description = description;
        this.chargeTime = chargeTime;
    }

    @Override
    public String toString() {
        return String.format("Name: %s \n %s \nCooldown: %s ticks", this.name, this.description, this.chargeTime);
    }

    public int getChargeTime() {
        return this.chargeTime;
    }

    public abstract void execute(Character target);

    public void executeChar() {execute(target);};

    public abstract void execute(Building target);

    public abstract int getValue();

    public void setTarget(Character target) {
        this.target = target;
    }

    public void setHitAll(boolean tf) {
        this.hitAll = tf;
    }

    public Character getTarget() {
        return target;
    }

    public boolean getHitAll() {
        return hitAll;
    }
}
