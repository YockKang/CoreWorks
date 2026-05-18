package com.main.CoreWorks.Resources;

public class Resource {
    String id;
    String name;
    float dmgMult;

    // possibly more multipliers?

    public Resource (String idIn, String nameIn, float dmg) {
        id = idIn;
        name = nameIn;
        dmgMult = dmg;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public float getDmgMult() {
        return dmgMult;
    }
}
