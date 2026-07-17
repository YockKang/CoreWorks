package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.*;

public class EnemyFactory {
    JsonValue data;
    String id;

    public EnemyFactory(JsonValue dataIn) {
        data = dataIn;
        id = data.getString("id");
    }

    @Override
    public String toString() {
        return "Factory of Enemy: " + id;
    }

    public Enemy of(float mult) {
        return new Enemy(data, mult);
    }

    public JsonValue getData() {
        return data;
    }
}
