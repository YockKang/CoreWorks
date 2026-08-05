package com.main.CoreWorks.util;

public class MathExtras {
    public static float roundDP(float input, int n) {
        return (float) (java.lang.Math.round((double) input * java.lang.Math.pow(10, n)) / java.lang.Math.pow(10, n));
    }

    public static double roundDP(double input, int n) {
        return (java.lang.Math.round( input * java.lang.Math.pow(10, n)) / java.lang.Math.pow(10, n));
    }
}
