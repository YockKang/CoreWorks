package com.main.CoreWorks.Codex;

import com.main.CoreWorks.database.RelicDatabase;
import com.main.CoreWorks.entities.Relics.Relic;

public class RelicEntry extends Entry{
    protected Relic relic;

    public RelicEntry(Relic relic) {
        super(relic.getId(), relic.getName(), relic.getCodexDescription());
    }
}
