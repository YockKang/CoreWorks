package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.ResourceRequest.ResourceRequest;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.moveset.Move;

public interface Updatable {

    Array<Move> updateTick();

    Array<ResourceRequest> generateDemandRequests(RunState runState);
}
