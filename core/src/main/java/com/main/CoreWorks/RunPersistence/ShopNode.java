package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Rewards.ShopOffer;

public class ShopNode extends MapNode{

    public ShopNode(int tier, float multiplier, float x, float y) {
        this.tier = tier;
        this.rewardMultiplier = multiplier;
        this.x = x;
        this.y = y;
        this.name = "Shop";
    }

    public ShopNode(int tier, int row, int col, float multiplier, float x, float y) {
        this.tier = tier;
        this.rewardMultiplier = multiplier;
        this.x = x;
        this.y = y;
        this.row = row;
        this.col = col;
        this.name = "Shop";
    }

}
