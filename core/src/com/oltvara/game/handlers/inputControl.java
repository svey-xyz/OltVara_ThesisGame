package com.oltvara.game.handlers;

public class inputControl {

    public static boolean[] keys;
    public static boolean[] prevKeys;

    public static final int lenKEYS = 3;
    public static final int JUMPBUT = 0;
    public static final int RIGHT = 1;
    public static final int LEFT = 2;

    private static int[] heldTime;

    static {
        keys = new boolean[lenKEYS];
        prevKeys = new boolean[lenKEYS];
        heldTime = new int[lenKEYS];
    }

    public static void update() {
        System.arraycopy(keys, 0, prevKeys, 0, lenKEYS);

        for (int i = 0; i < lenKEYS; i++) {
            if (keys[i]) {
                heldTime[i]++;
            }
        }
    }

    static void setKey(int i, boolean b) {
        keys[i] = b;
        if (b) { heldTime[i] = 0; }
    }

    public static int heldTime(int i) { return heldTime[i]; }

    public static boolean isReleased(int i) {
        return prevKeys[i] && !keys[i];
    }

    public static boolean isPressed(int i) {
        return keys[i];
    }

    public static boolean isTap(int i) {
        return keys[i] && !prevKeys[i];
    }

}
