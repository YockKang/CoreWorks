package com.main.CoreWorks.Factory.ResourceRequest;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;

public class WhitelistRequest extends ResourceRequest {

    protected Array<String> whitelist;

    public WhitelistRequest(Building building, int value, Array<String> rsc){
        super(null, building, value, 1);
        whitelist = rsc;
    }

    @Override
    public String toString() {
        return requester.displayName() + " requests " + value + " any of: " + resource + " @P: " + priority;
    }

    public Array<String> getWhitelist() {
        return whitelist;
    }

}
