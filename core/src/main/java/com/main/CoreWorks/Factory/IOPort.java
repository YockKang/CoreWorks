package com.main.CoreWorks.Factory;

public class IOPort {
    protected int xCoord;
    protected int yCoord;
    protected int dir;
    static final String[] types = {"IN", "OUT"};
    protected String type;

    public IOPort(int x, int y, int d, String typ) {
        this.xCoord = x;
        this.yCoord = y;
        this.dir = d;
        this.type = typ;
    }


}
