package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.JsonValue;

public class StatusEffect {

    private String type;
    private int value;
    private int duration;
    private int currDuration;
    private float reductionMultiplier;
    private boolean onSelf;

    public StatusEffect(String type, int value, int dur, float reductionMultiplier, boolean immediateAct, boolean onSelf) {
        this.type = type;
        this.value = value;
        this.duration = dur;
        this.onSelf = onSelf;
        if (immediateAct) {
            this.currDuration = 0;
        } else {
            this.currDuration = duration;
        }
        this.reductionMultiplier = reductionMultiplier;
    }

    public StatusEffect(JsonValue data, float multiplier) {
        this.type = data.getString("Type");
        this.value = (int) (data.getInt("Value") * multiplier);
        this.duration = data.getInt("Duration");
        this.onSelf = data.getBoolean("OnSelf");
        boolean immediateAct = data.getBoolean("Immediate");
        if (immediateAct) {
            this.currDuration = 0;
        } else {
            this.currDuration = duration;
        }
        this.reductionMultiplier = data.getFloat("Reduction");
    }

    public int tick() {
        if (currDuration == 0) {
            int oldValue = value;
            value = (int) (value * reductionMultiplier);
            currDuration = duration;
            return oldValue;
        } else {
            currDuration--;
            return 0;
        }
    }

    public String getType() {
        return type;
    }

    public void addValue(int delta) {
        this.value += delta;
    }

    public int getValue() {
        return value;
    }

    public boolean isOnSelf() {
        return onSelf;
    }

    public int getCurrDuration() {
        return currDuration;
    }

    public float getReductionMultiplier() {
        return reductionMultiplier;
    }
}
