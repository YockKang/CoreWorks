package com.main.CoreWorks.Factory.Tubes;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.Building;

public class TubeNet {
    private Array<Building> inputs;
    private Array<Tube> components;

    public TubeNet() {
        inputs = new Array<>();
    }

    public TubeNet(Array<Building> bldg) {
        inputs = bldg;
    }

    public Array<Building> getInputs() {
        return inputs;
    }

    public void addSegment(Tube newComponent) {
        components.add(newComponent);
    }

    public void addSegment(Array<Tube> newComponent) {
        components.addAll(newComponent);
    }

    public Array<Tube> getComponents() {
        return components;
    }

    public void setNetwork(TubeNet tubeNet) {
        components.forEach(tb -> tb.setNetwork(this, tubeNet));
    }

    public void addInput(Building input) {
        inputs.add(input);
    }

    public void addInput(Array<Building> inputs) {
        inputs.addAll(inputs);
    }
}
