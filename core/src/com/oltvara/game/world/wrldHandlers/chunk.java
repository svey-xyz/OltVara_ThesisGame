package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.mainGame;
import com.oltvara.game.world.map;
import com.oltvara.game.world.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static com.oltvara.game.mainGame.numPowerTILES;

public class chunk {

    private ConcurrentHashMap<Vector2, tile> tiles;
    private int[] heightMap;
    private final int LEN;
    private final int LEFTPT, RIGHTPT, OFFSET;
    private float displace, roughness;
    private Vector2 pos;
    private tile tl;
    private map map;

    private HashMap<String, Vector2> neighbours;
    private chunk nCH;

    public chunk(int leftHeight, int rightHeight, int displace, float roughness, int offset) {
        this.displace = displace;
        this.roughness = roughness;

        this.LEFTPT = leftHeight;
        this.RIGHTPT = rightHeight;

        this.LEN = (int)(mainGame.cHEIGHT / mainGame.TILESIZE);
        neighbours = new HashMap<String, Vector2>();

        this.OFFSET = offset * (numPowerTILES + 1);

        map = play.getMapControl();

        heightMap = new int[numPowerTILES + 1];

        tiles = new ConcurrentHashMap<Vector2, tile>();

        sideTiles();
        midPoint();

        updateTiles();
    }

    private void updateTiles() {
        for (tile tl : tiles.values()) {
            getNeighbours(tl);
            if (!tiles.containsKey(neighbours.get("LEFT")) && !tiles.containsKey(neighbours.get("RIGHT")) && !tl.isEDGE()) {
                tl.updateTexture(tl.liveDGroundNames, 1);
            } else if (!tiles.containsKey(neighbours.get("RIGHT")) && !tl.isRIGHT()) {
                tl.updateTexture(tl.liveGroundNames, 1);
            } else {
                tl.updateTexture(tl.groundNames, 1);
            }
        }
    }

    private void getNeighbours(tile tl) {
        neighbours.clear();
        neighbours.put("RIGHT", new Vector2(tl.getPosition().x + 1, tl.getPosition().y));
        neighbours.put("LEFT", new Vector2(tl.getPosition().x - 1, tl.getPosition().y));
        neighbours.put("TOP", new Vector2(tl.getPosition().x, tl.getPosition().y + 1));
    }

    public void render(SpriteBatch sb) {
        for(tile tl : tiles.values()){
            tl.render(sb);
        }
    }

    private void sideTiles() {
        heightMap[0] = LEFTPT;
        heightMap[numPowerTILES] = RIGHTPT;

        for (int h = LEFTPT; h >= 0; h--) {
            pos = new Vector2(0, h);
            tl = new tile(pos, OFFSET, "LEFT");
            tiles.put(pos, tl);
        }

        for (int h = RIGHTPT; h >= 0; h--) {
            pos = new Vector2(numPowerTILES, h);
            tl = new tile(pos, OFFSET, "RIGHT");
            tiles.put(pos, tl);
        }
    }

    private void midPoint() {
        //Midpoint algorithm by Loktar.
        for (int i = 1; i < numPowerTILES; i *= 2) {
            // Iterate through each segment calculating the center point
            for (int j = (numPowerTILES / i) / 2; j < numPowerTILES; j += numPowerTILES / i) {
                heightMap[j] = ((heightMap[j - (numPowerTILES / i) / 2] + heightMap[j + (numPowerTILES / i) / 2]) / 2);
                heightMap[j] += (Math.random() * displace * 2) - displace;

                for (int y = heightMap[j]; y >=0; y--) {
                    pos = new Vector2(j, y);
                    tl = new tile(pos, OFFSET, "");

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
