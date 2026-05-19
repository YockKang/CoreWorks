package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.moveset.Move;

public abstract class Building {


    // confirmed fields
    protected boolean isEnabled = true;
    protected boolean onGrid = false;
    protected int cooldownTimer;
    protected int currCooldown = 0;
    protected Array<Integer> inputBufferSize;
    protected Array<Integer> outputBufferSize;
    protected Array<Integer> inputBuffer;
    protected Array<Integer> outputBuffer;
    protected int xCoord = -1; // bottom is 0
    protected int yCoord = -1; // left is 0
    protected int rotation = 0; // 0 is "up"
    protected String name;
    protected Recipe recipe = null;
    /*
    protected Queue<IOPort> inPorts;
    protected Queue<IOPort> outPorts;
     */


    protected boolean[][][] shape;
    /*
    SHAPE GUIDE
    stores all rotations and then the 2d representation of which tiles are filled

    e.g. 1 by 1

    { {{true}}, {{true}}, {{true}}, {{true}} }

    e.g. 2 by 2 L

    { {{true, true},
       {true, false}},

      {{true, false},
       {true, true}},

      {{false, true},
       {true, true}},

      {{true, true},
       {false, true}} }
     */

    // ?? fields
    protected int HP;

    public Building(int coolDown,
                    Array<Integer> inBuffer,
                    Array<Integer> outBuffer,
                    Array<Integer> inputs,
                    Array<Integer> outputs,
                    boolean[][][] shape,
                    String nameIn) {
        cooldownTimer = coolDown;
        inputBufferSize = inBuffer;
        outputBufferSize = outBuffer;
        inputBuffer = inputs;
        outputBuffer = outputs;
        name = nameIn;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract Move updateTick();

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

    public void toggleEnable() {
        if (isEnabled) {
            disable();
        } else {
            enable();
        }
    }

    public void clear() {
        for (Integer q : inputBuffer) {
            q = 0;
        }
        for (Integer q : outputBuffer) {
            q = 0;
        }
        currCooldown = 0;
    }

    public void setPos(int x, int y) {
        xCoord = x;
        yCoord = y;
    }

    public int[] getPos() {
        return new int[] {getX(), getY()};
    }

    public int getX() {
        return xCoord;
    }

    public int getY() {
        return yCoord;
    }

    public boolean[][] getShape() {
        return shape[rotation];
    }

    public void setRotation(int rot) {
        rotation = rot;
    }

    public void putOnGrid() {
        onGrid = true;
    }

    public void takeOffGrid() {
        onGrid = false;
    }

    /*
    public void addPort(IOPort port) {
        inPorts.addLast(port);
    }

    public void addInPort(int x, int y, int dir) {
        IOPort port = new IOPort(x, y, dir, "IN");
        inPorts.addLast(port);
    }

    public void addOutPort(int x, int y, int dir) {
        IOPort port = new IOPort(x, y, dir, "OUT");
        outPorts.addLast(port);
    }

    public void clearInPorts() {
        inPorts.clear();
    }

    public void clearOutPorts() {
        outPorts.clear();
    }

    public void clearAllPorts() {
        clearInPorts();
        clearOutPorts();
    }
     */

    /*
	    Bool isEnabled
	    int cooldownTimer
	    int currCooldown
	    Arr inputBuffer [as a queue]
	    Arr outputBuffer [as a queue]
	    int inputLimit
	    int outputLimit
	    int HP?????
	    [x, y] coords
	    String Name
	    int id
	    Something upgrade?
	    [[hw]] shape
	    I/O limitations [TBA]
	    updateTick()
	    enable()
	    disable()
	    toggleEnable()
	    getters()
	    clear()
     */
}
