package com.main.CoreWorks.database;

import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.moveset.DamageMove;

public class EnemyDatabase {

    public static Enemy createMissileDrone() {
        Enemy enemy = new Enemy(10, 0, "Missile Drone", 3);
        enemy.addMove(new DamageMove(2, 2));
        return enemy;
    }
}
