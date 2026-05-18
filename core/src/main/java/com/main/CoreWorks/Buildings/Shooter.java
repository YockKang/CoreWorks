package com.main.CoreWorks.Buildings;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.DamageMove;
import com.main.CoreWorks.moveset.Move;

public class Shooter extends Building {

    protected Queue<Resource> magazine;
    protected int magSize;
    protected float baseDmg;

    public Shooter(int coolDown, int magSize, boolean[][][] shape) {
        super(coolDown,
            new Array<Integer>(0),
            new Array<Integer>(0),
            new Array<Integer>(0),
            new Array<Integer>(0),
            shape,
            "shooter");
        this.magazine = new Queue<>(magSize);
        this.magSize = magSize;
    }


    @Override
    public String toString() {
        return new StringBuilder()
            .append(name)
            .append(" ")
            .append(id)
            .append('\n')
            .append("Magazine\n")
            .append("<-First   Last->")
            .append(magazine)
            .toString();
    }

    @Override
    public Move updateTick() {
        if (currCooldown >= cooldownTimer && magazine.notEmpty()) {
            currCooldown = 0;
            return shoot();
        } else {
            currCooldown++;
        }
        return null;
    }

    public DamageMove shoot() {
        return new DamageMove((int) (magazine.removeFirst().getDmgMult() * baseDmg), 0);
    }
}
