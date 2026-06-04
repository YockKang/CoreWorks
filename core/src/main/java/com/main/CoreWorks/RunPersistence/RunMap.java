package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.Array;

public class RunMap {
    private Array<MapNode> nodes = new Array<>();
    private MapNode startNode;

    public Array<MapNode> getNodes() {
        return nodes;
    }

    public void addNode(MapNode node) {
        this.nodes.add(node);
    }

    public MapNode getStartNode() {
        return startNode;
    }

    public void setStartNode(MapNode startNode) {
        this.startNode = startNode;
    }
}
