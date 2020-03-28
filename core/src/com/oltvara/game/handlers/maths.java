package com.oltvara.game.handlers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/*My math functions class. Some of these I recreated based on processing expressions.
Color functions to work around LibGDX color library.
Each is pretty self explanatory.
Perlin noise function was found online and is based on the creative commons attribution of the original function */
public class maths {

    public Color fromRGB(int r, int g, int b) {
        float RED = r / 255.0f;
        float GREEN = g / 255.0f;
        float BLUE = b / 255.0f;
        return new Color(RED, GREEN, BLUE, 1);
    }

    public double map(double x, double cMin, double cMax, double nMin, double nMax) {
        return (x - cMin) * (nMax - nMin) / (cMax - cMin) + nMin;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double random(double min, double max) {
        return (min + ThreadLocalRandom.current().nextFloat() * (max - min));
    }

    public double random() {
        return ThreadLocalRandom.current().nextFloat();
    }

    public Integer randomInt(int bounds) {
        return ThreadLocalRandom.current().nextInt(bounds);
    }

    public double randomGaussian(double standDev, double mean) {
        return ((ThreadLocalRandom.current().nextGaussian() * standDev) + mean);
    }

    public Color randomCol(Color cCol) {
        float r, g, b;
        r = (float)(constrain((random() + 1) * cCol.r, 0 , 255));
        g = (float)(constrain((random() + 1) * cCol.g, 0 , 255));
        b = (float)(constrain((random() + 1) * cCol.b, 0 , 255));

        return new Color (r, g, b, 1);
    }

    public Color gaussianCol(Color col, float sDCol) {
        float[] colValTemp = new float[] {col.r, col.g, col.b};

        //adjust each rgb value by a gaussian curve
        for (int j = 0; j < 3; j++) {
            double rnd = randomGaussian(sDCol, 0);
            colValTemp[j] += rnd;
            colValTemp[j] = (float)constrain(colValTemp[j], 0, 255);
        }

        //creates LibGDX color
        return new Color(colValTemp[0], colValTemp[1], colValTemp[2], 1);
    }

    public double constrain(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public double constrain(double value, double min) {
        return Math.max(value, min);
    }

    public Vector3 roundVec(Vector3 vec) {
        return new Vector3((int)vec.x, (int)vec.y, (int)vec.z);
    }

    public Vector2 roundVec(Vector2 vec) {
        return new Vector2((int)vec.x, (int)vec.y);
    }

    public float distance(Vector2 object1, Vector2 object2){
        return (float)Math.sqrt(Math.pow((object2.x - object1.x), 2) + Math.pow((object2.y - object1.y), 2));
    }

    public float distance(float x1, float y1, float x2, float y2){
        return (float)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public float nearEdge(int x, int z, int CENTRE_POINT_X, int CENTRE_POINT_Y) {
        return (float)Math.floor(distance(x, z, CENTRE_POINT_X, CENTRE_POINT_Y));
    }

    public double percentDifference(double val1, double val2) {
        if (val1 > 0) {
            return (Math.abs(val1 - val2) / val1);
        } else {
            return val2;
        }
    }

    public double lerp(double amount, double cVal, double nVal) {
        return ((1 - amount) * cVal + amount * nVal);
    }

    //for Perlin noise
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public double PerlinNoise(double x, double y, double smoothness, int rep) {
        float total = 0;

        for (int i = 0; i <= (rep - 1); i++) {
            float freq = (float) Math.pow(2, i);
            double amp = Math.pow(smoothness, i);
            total += noise((x * freq) * amp, (y * freq) * amp);
        }

        return total;
    }

    //for Perlin noise
    private double noise(double x, double y){
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;
        int g1 = p[p[xi] + yi];
        int g2 = p[p[xi + 1] + yi];
        int g3 = p[p[xi] + yi + 1];
        int g4 = p[p[xi + 1] + yi + 1];

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        double d1 = grad(g1, xf, yf);
        double d2 = grad(g2, xf - 1, yf);
        double d3 = grad(g3, xf, yf - 1);
        double d4 = grad(g4, xf - 1, yf - 1);

        double u = fade(xf);
        double v = fade(yf);

        double x1Inter = lerp(u, d1, d2);
        double x2Inter = lerp(u, d3, d4);

        return lerp(v, x1Inter, x2Inter);

    }

    //for Perlin noise
    private double grad(int hash, double x, double y){
        switch(hash & 3){
            case 0: return x + y;
            case 1: return -x + y;
            case 2: return x - y;
            case 3: return -x - y;
            default: return 0;
        }
    }

    //Seeding Perlin noise
    private static final int[] p = new int[512];
    private static final int[] permutation = { 151,160,137,91,90,15,
            131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
            190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
            88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
            77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
            102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
            135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
            5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
            223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
            129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
            251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
            49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
            138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
    };

    static { for (int i=0; i < 256 ; i++) p[256+i] = p[i] = permutation[i]; }
}


