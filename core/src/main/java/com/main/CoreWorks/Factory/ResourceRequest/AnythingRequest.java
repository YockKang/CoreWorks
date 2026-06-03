package com.main.CoreWorks.Factory.ResourceRequest;

import com.main.CoreWorks.Factory.Building;

public class AnythingRequest extends ResourceRequest {

    public AnythingRequest(Building building, int value) {
        super(null, building, value, 2);
    }


    @Override
    public String toString() {
        return requester.displayName() + " requests " + value + " anything @P: " + priority;
    }

}
