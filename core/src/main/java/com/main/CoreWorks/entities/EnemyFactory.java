package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.*;

public class EnemyFactory {
    JsonValue data;

    public EnemyFactory(JsonValue dataIn) {
        data = dataIn;
    }

    public Enemy of(float mult) {
        return new Enemy(data, mult);
    }
}
