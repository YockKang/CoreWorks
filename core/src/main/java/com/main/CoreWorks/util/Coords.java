package com.main.CoreWorks.util;

public class Coords {
    /*
    Helper Coordinate class to easily store x and y coordinates (something like Pair class from lectures)
     */

    public final int x;
    public final int y;


    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coords(int[] coords) {
        if (coords.length > 0) {
            this.x = coords[0];
        } else {
            this.x = 0;
        }
        if (coords.length > 1) {
            this.y = coords[1];
        } else {
            this.y = 0;
        }
    }

    public static Coords origin() {
        return new Coords(0, 0);
    }

    public DirectedCoords addDirection(int dir) {
        return new DirectedCoords(x, y, dir);
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }
}


