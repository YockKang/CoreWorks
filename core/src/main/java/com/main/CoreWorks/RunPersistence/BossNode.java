package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.entities.Enemy;

public class BossNode extends CombatNode{

    public BossNode(Array<Enemy> enemies, int tier, float multiplier, float x, float y) {
        super(enemies, tier, multiplier, x, y);
        this.name = "Boss";
    }

    public BossNode(Array<Enemy> enemies, int row, int col, int tier, float multiplier, float x, float y) {
        super(enemies, row, col, tier, multiplier, x, y);
        this.name = "Boss";
    }
}
