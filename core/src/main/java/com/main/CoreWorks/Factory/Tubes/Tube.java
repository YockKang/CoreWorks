package com.main.CoreWorks.Factory.Tubes;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Structure;

public class Tube extends Structure {
    protected boolean hasDouble = false;
    protected TubeNet network1;
    protected TubeNet network2;
    protected final boolean[] connections1;
    protected final boolean[] connections2;
    protected boolean fullConnect = false;

    public Tube(int x, int y) {
        super(x, y);
        connections1 = new boolean[] {false, false, false, false};
        connections2 = new boolean[] {false, false, false, false};
    }

    public Tube(int x, int y, boolean[] conn) {
        super(x, y);
        connections1 = conn;
        connections2 = new boolean[] {false, false, false, false};
    }

    public Tube(int x, int y, int type) {
        super(x, y);
        switch (type) {
            case 0 -> {
                connections1 = new boolean[] {true, false, true, false};
                connections2 = new boolean[] {false, false, false, false};
            }
            case 1 -> {
                connections1 = new boolean[] {true, true, false, false};
                connections2 = new boolean[] {false, false, false, false};
            }
            case 2 -> {
                connections1 = new boolean[] {true, true, true, false};
                connections2 = new boolean[] {false, false, false, false};
            }
            case 3 -> {
                connections1 = new boolean[] {true, true, true, true};
                connections2 = new boolean[] {false, false, false, false};
                fullConnect = true;
            }
            case 4 -> {
                connections1 = new boolean[] {true, true, false, false};
                connections2 = new boolean[] {false, false, true, true};
                hasDouble = true;
                fullConnect = true;
            }
            case 5 -> {
                connections1 = new boolean[] {true, false, true, false};
                connections2 = new boolean[] {false, true, false, true};
                hasDouble = true;
                fullConnect = true;
            }
            default -> {
                connections1 = new boolean[]{false, false, false, false};
                connections2 = new boolean[]{false, false, false, false};
            }
        }
    }

    /*
    public void connect(Array<Array<Structure>> grid) {
        Array<TubeNet> neighbourNets = new Array<>(4);
        Array<Building> neighbourBldg = new Array<>(4);
        for (int i = 0; i < connections1.length; i++) {
            int[] gc = getGlobalCoord(0, 0);
            int trueDir = (rotation + i) % 4;
            if (!connections1[trueDir]) {
                continue;
            }
            switch (trueDir) {
                case 0 -> {
                    gc[1]--;
                }
                case 1 -> {
                    gc[0]++;
                }
                case 2 -> {
                    gc[1]++;
                }
                case 3 -> {
                    gc[0]--;
                }
            }
            Building maybeNeighbour = null;
            try {
                maybeNeighbour = grid.get(gc[1]).get(gc[0]);
            } catch (Exception e) {
                continue;
            }
            if (maybeNeighbour != null) {
                if (maybeNeighbour instanceof Tube tubeNeighbour) {
                    neighbourNets.add(tubeNeighbour.getNetwork((trueDir + 2) % 4));
                } else {
                    if (maybeNeighbour.hasPortFor(gc[0], gc[1], (trueDir + 2) % 4)) {
                        neighbourBldg.add(maybeNeighbour);
                    }
                }
            }
        }
        switch (neighbourNets.size) {
            case 0:
                network1 = new TubeNet(neighbourBldg);
                network1.addSegment(this);
                break;
            case 1:
                network1 = neighbourNets.get(0);
                network1.addSegment(this);
            default:
                int largestIdx = 0;
                int largestVal = 0;
                for (int i = 0; i < neighbourNets.size; i++) {
                    if (neighbourNets.get(i).getComponents().size < largestVal) {
                        largestIdx = i;
                        largestVal = neighbourNets.get(i).getComponents().size;
                    }
                }
                for (int i = 0; i < neighbourNets.size; i++) {
                    if (i == largestIdx) {
                        continue;
                    }
                    neighbourNets.get(largestIdx).addSegment(neighbourNets.get(i).getComponents());
                    neighbourNets.get(i).setNetwork(neighbourNets.get(largestIdx));
                    neighbourNets.get(largestIdx).addInput(neighbourNets.get(i).getInputs());
                    neighbourNets.get(largestIdx).getInputs().sort((a, b)  -> a.getPriority() - b.getPriority());
                }
        }
        if (hasDouble) {
            neighbourNets.clear();
            neighbourBldg.clear();
            for (int i = 0; i < connections2.length; i++) {
                int[] gc = getGlobalCoord(0, 0);
                int trueDir = (rotation + i) % 4;
                if (!connections2[trueDir]) {
                    continue;
                }
                switch (trueDir) {
                    case 0 -> {
                        gc[1]--;
                    }
                    case 1 -> {
                        gc[0]++;
                    }
                    case 2 -> {
                        gc[1]++;
                    }
                    case 3 -> {
                        gc[0]--;
                    }
                }
                Building maybeNeighbour = null;
                try {
                    maybeNeighbour = grid.get(gc[1]).get(gc[0]);
                } catch (Exception e) {
                    continue;
                }
                if (maybeNeighbour != null) {
                    if (maybeNeighbour instanceof Tube tubeNeighbour) {
                        neighbourNets.add(tubeNeighbour.getNetwork((trueDir + 2) % 4));
                    } else {
                        if (maybeNeighbour.hasPortFor(gc[0], gc[1], (trueDir + 2) % 4)) {
                            neighbourBldg.add(maybeNeighbour);
                        }
                    }
                }
            }
            switch (neighbourNets.size) {
                case 0:
                    network2 = new TubeNet(neighbourBldg);
                    network2.addSegment(this);
                    break;
                case 1:
                    network2 = neighbourNets.get(0);
                    network2.addSegment(this);
                default:
                    int largestIdx = 0;
                    int largestVal = 0;
                    for (int i = 0; i < neighbourNets.size; i++) {
                        if (neighbourNets.get(i).getComponents().size < largestVal) {
                            largestIdx = i;
                            largestVal = neighbourNets.get(i).getComponents().size;
                        }
                    }
                    for (int i = 0; i < neighbourNets.size; i++) {
                        if (i == largestIdx) {
                            continue;
                        }
                        neighbourNets.get(largestIdx).addSegment(neighbourNets.get(i).getComponents());
                        neighbourNets.get(i).setNetwork(neighbourNets.get(largestIdx));
                        neighbourNets.get(largestIdx).addInput(neighbourNets.get(i).getInputs());
                        neighbourNets.get(largestIdx).getInputs().sort((a, b)  -> a.getPriority() - b.getPriority());
                    }
            }
        }
    }

    @Override
    public void updateOutputs(Array<Array<Building>> grid) {}

    public void disconnect(Array<Array<Building>> grid) {
        onGrid = false;
        Array<Tube> neighbourSeg = new Array<>();
        for (int i = 0; i < connections1.length; i++) {
            int[] gc = getGlobalCoord(0, 0);
            int trueDir = (rotation + i) % 4;
            if (!connections1[trueDir]) {
                continue;
            }
            switch (trueDir) {
                case 0 -> {
                    gc[1]--;
                }
                case 1 -> {
                    gc[0]++;
                }
                case 2 -> {
                    gc[1]++;
                }
                case 3 -> {
                    gc[0]--;
                }
            }
            Building maybeNeighbour = null;
            try {
                maybeNeighbour = grid.get(gc[1]).get(gc[0]);
            } catch (Exception e) {
                continue;
            }
            if (maybeNeighbour instanceof Tube tube) {
                neighbourSeg.add(tube);
            }
        }


    }

    protected void searchConnected(boolean[] connections1, boolean[] connections2){}


     */

    public void setNetwork(TubeNet oldNet, TubeNet newNet) {
        if (network1 == oldNet) {
            network1 = newNet;
        }
        if (network2 == oldNet) {
            network2 = newNet;
        }
    }

    public void setNetwork(int dir, TubeNet newNet) {
        if (connections1[dir]) {
            network1 = newNet;
        }
        if (connections1[dir]) {
            network2 = newNet;
        }
    }


    public TubeNet getNetwork(int dir) {
        if (connections1[dir]) {
            return network1;
        }
        if (connections2[dir]) {
            return network2;
        }
        return null;
    }


    public void connect(Array<Array<Structure>> grid) {}

    public void disconnect(Array<Array<Structure>> grid) {}

    public boolean getDouble() { return hasDouble; }

    public boolean[] getConnections1() { return connections1; }

    public boolean[] getConnections2() { return connections2; }

    public void addConnection(int dir1, int dir2) {
        if (!hasDouble && !fullConnect) {
            if (connections1[dir1] ^ connections1[dir2]) {
                connections1[dir1] = true;
                connections1[dir2] = true;
                if (connections1[0] && connections1[1] && connections1[2] && connections1[3]) {
                    fullConnect = true;
                }

            } else if (!connections1[dir1] && !connections1[dir2]) {
                hasDouble = true;
                connections2[dir1] = true;
                connections2[dir2] = true;
                fullConnect = true;
            }
        }
    }

    private static class TubeSearcher {
        Array<Array<Building>> grid;
        TubeSearcher (Array<Array<Building>> gridIn) {
            grid = gridIn;
        }

        public boolean allConnected(int x, int y, int dir, Array<Array<Integer>> locations) {
            return false;
        }
    }
}
