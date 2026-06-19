package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.entities.Enemy;

public class EliteNode extends MapNode{
    private Array<Enemy> enemies;

    public EliteNode(Array<Enemy> enemies, int tier, float multiplier, float x, float y) {
        this.enemies = enemies;
        this.tier = tier;
        this.rewardMultiplier = multiplier;
        this.x = x;
        this.y = y;
        this.name = "Elite";
    }

    public EliteNode(Array<Enemy> enemies, int row, int col, int tier, float multiplier, float x, float y) {
        this.enemies = enemies;
        this.tier = tier;
        this.rewardMultiplier = multiplier;
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
        this.name = "Elite";
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

}
