package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.oltvara.game.mainGame;
import com.oltvara.game.world.tile;

import java.util.concurrent.ConcurrentHashMap;

public class chunk {

    private ConcurrentHashMap<Vector2, tile> tiles;
    private int[] heightMap;
    private final int LEN;
    private final int LEFTPT, RIGHTPT, OFFSET;
    private float displace, roughness;
    private int lenPower2;
    private Vector2 pos;
    private tile tl;

    public chunk(int leftHeight, int rightHeight, int displace, float roughness, int offset) {
        this.displace = displace;
        this.roughness = roughness;

        this.LEFTPT = leftHeight;
        this.RIGHTPT = rightHeight;

        this.LEN = (int)(mainGame.cWIDTH / mainGame.TILESIZE);

        lenPower2 = (int)Math.pow(2, Math.ceil(Math.log(LEN) / (Math.log(2))));

        this.OFFSET = offset * (lenPower2 + 1);

        heightMap = new int[lenPower2 + 1];

        tiles = new ConcurrentHashMap<Vector2, tile>();

        sideTiles();
        midPoint();
    }

    public void updateTiles(){
    }

    public void render(SpriteBatch sb) {
        for(tile tl : tiles.values()){
            tl.render(sb);
        }
    }

    private void sideTiles() {
        heightMap[0] = LEFTPT;
        heightMap[lenPower2] = RIGHTPT;

        for (int h = LEFTPT; h >0; h--) {
            pos = new Vector2(0, h);
            tl = new tile(pos, this);
            tiles.put(pos, tl);
        }

        for (int h = RIGHTPT; h >0; h--) {
            pos = new Vector2(lenPower2, h);
            tl = new tile(pos, this);
            tiles.put(pos, tl);
        }
    }

    private void midPoint() {
        //Midpoint algorithm by Loktar.
        for (int i = 1; i < lenPower2; i *= 2) {
            // Iterate through each segment calculating the center point
            for (int j = (lenPower2 / i) / 2; j < lenPower2; j += lenPower2 / i) {
                heightMap[j] = ((heightMap[j - (lenPower2 / i) / 2] + heightMap[j + (lenPower2 / i) / 2]) / 2);
                heightMap[j] += (Math.random() * displace * 2) - displace;

                for (int y = heightMap[j]; y >=0; y--) {
                    pos = new Vector2(j, y);
                    tl = new tile(pos, this);

                    tiles.put(pos, tl);
                }
            }
            // reduce our random range
            displace *= roughness;
        }
    }

    public ConcurrentHashMap<Vector2, tile> getTileMap() {
        return tiles;
    }

    public int getOFFSET() {
        return OFFSET;
    }

    public int[] getHeightMap() {
        return heightMap;
    }
}
