package com.main.CoreWorks.RunPersistence;

public class RestNode extends MapNode{

    public RestNode(int tier, float multiplier, float x, float y) {
        this.tier = tier;
        this.rewardMultiplier = multiplier;
        this.x = x;
        this.y = y;
        this.name = "Rest";
    }

    public RestNode(int tier, int row, int col, float multiplier, float x, float y) {
        this.tier = tier;
        this.rewardMultiplier = multiplier;
        this.x = x;
        this.y = y;
        this.row = row;
        this.col = col;
        this.name = "Rest";
    }

}
