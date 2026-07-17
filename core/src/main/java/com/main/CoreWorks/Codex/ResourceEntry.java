package com.main.CoreWorks.Codex;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Modifier;
import com.main.CoreWorks.database.RecipeDatabase;

import java.util.Objects;

public class ResourceEntry extends Entry{

    protected  float dmgMult;
    protected String damageType = null;
    protected Array<RecipeEntry> asInput = new Array<>();
    protected Array<RecipeEntry> asOutput = new Array<>();

    public ResourceEntry(JsonValue data) {
        super(data);
        dmgMult = data.getFloat("DmgMult");
        if (data.get("Modifiers") != null) {
            JsonValue mods = data.get("Modifiers");
            if (mods.isArray()) {
                for (JsonValue mod : mods) {
                    String type = mod.getString("type");
                    switch (type) {
                        case "DamageType" -> {
                            damageType = mod.getString("str");
                        }
                    }
                }
            }
            if (damageType == null) {
                damageType = "Normal";
            }
        }
    }

    protected void addAsInput(RecipeEntry r) {
        asInput.add(r);
    }

    protected void addAsOutput(RecipeEntry r) {
        asOutput.add(r);
    }
}
