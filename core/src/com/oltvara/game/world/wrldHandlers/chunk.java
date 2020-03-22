package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.world.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static com.oltvara.game.mainGame.*;

public class chunk {

    private ConcurrentHashMap<Vector2, tile> tiles;
    private ArrayList<Body> bodies;
    private int[] heightMap;
    private final int LEFTPT, RIGHTPT, OFFSET;
    private float displace, roughness;
    private Vector2 pos;
    private tile tl;
    private Vector2 std;
    private static Vector2 bottomLeftViewpoint, viewportSize;

    private HashMap<String, Vector2> neighbours;
    private HashMap<Vector2, Integer> tileSpacing;

   private forest forestHandlerBack, forestHandlerFront;

    public chunk(int leftHeight, int rightHeight, int displace, float roughness, int offset) {
        this.displace = displace;
        this.roughness = roughness;

        this.LEFTPT = leftHeight;
        this.RIGHTPT = rightHeight;

        neighbours = new HashMap<String, Vector2>();
        tileSpacing = new HashMap<Vector2, Integer>();

        this.OFFSET = offset * (numTILES);

        heightMap = new int[numTILES];

        std = new Vector2(1, 1);
        viewportSize = play.getViewporSize();

        tiles = new ConcurrentHashMap<Vector2, tile>();
        bodies = new ArrayList<Body>();

        sideTiles();
        midPoint();

        updateTiles();
        forestHandlerBack = new forest(OFFSET, tileSpacing, heightMap);
        forestHandlerFront = new forest(OFFSET, tileSpacing, heightMap);
    }

    private void sideTiles() {
        heightMap[0] = (int)fct.constrain(LEFTPT, 0);
        heightMap[numPowerTILES] = (int)fct.constrain(RIGHTPT, 0);

        for (int h = LEFTPT; h >= 0; h--) {
            pos = new Vector2(0, h);
            tl = new tile(pos, OFFSET);
            tiles.put(pos, tl);
        }

        for (int h = RIGHTPT; h >= 0; h--) {
            pos = new Vector2(numPowerTILES, h);
            tl = new tile(pos, OFFSET);
            tiles.put(pos, tl);
        }
    }

    private void midPoint() {
        //Midpoint algorithm adapted from a method by Loktar.
        for (int i = 1; i < numPowerTILES; i *= 2) {
            //Iterate through each segment calculating the center point
            for (int j = (numPowerTILES / i) / 2; j < numPowerTILES; j += numPowerTILES / i) {
                heightMap[j] = ((heightMap[j - (numPowerTILES / i) / 2] + heightMap[j + (numPowerTILES / i) / 2]) / 2);
                heightMap[j] += (Math.random() * displace * 2) - displace;

                heightMap[j] = (int)fct.constrain(heightMap[j], 0);

                //place tiles at all height vals per column not just top
                for (int y = heightMap[j]; y >=0; y--) {
                    pos = new Vector2(j, y);
                    tl = new tile(pos, OFFSET);

                    tiles.put(pos, tl);
                }
            }
            // reduce our random range
            displace *= roughness;
        }
    }

    private void updateTiles() {
        for (tile tl : tiles.values()) {
            getNeighbours(tl);

            //Set correct texture based on neighbours
            int yFlip = new Random().nextBoolean() ? -1 : 1;
            if (!tiles.containsKey(neighbours.get("LEFT")) && !tiles.containsKey(neighbours.get("RIGHT")) && isEDGE(tl) == 0) {
                tl.updateTexture(pickTx(frTex.DOUBLELIVEGROUND), 1, yFlip);
            } else if (!tiles.containsKey(neighbours.get("RIGHT")) && isEDGE(tl) != 1) {
                tl.updateTexture(pickTx(frTex.LIVEGROUND), 1, yFlip);
            } else if (!tiles.containsKey(neighbours.get("LEFT")) && isEDGE(tl) != -1) {
                tl.updateTexture(pickTx(frTex.LIVEGROUND), -1, yFlip);
            } else {

                int xFlip = 1;
                yFlip = 1;

                int groundTex;

                if (fct.random() > 0.06) {
                    groundTex = frTex.GROUND;
                    xFlip = new Random().nextBoolean() ? -1 : 1;
                    yFlip = new Random().nextBoolean() ? -1 : 1;
                } else {
                    groundTex = frTex.ROCKS;
                }

                tl.updateTexture(pickTx(groundTex), xFlip, yFlip);
            }

            //add grass
            if (!tiles.containsKey(neighbours.get("TOP"))) {
                tl.addGrass(pickTx(frTex.GRASS));
            }

            //only create physics body if tile is reachable
            //should help with performance
            if (!hasNeighbours(tl)) {
                Body bod = play.boxWorld.createBody(tl.createBodDef(OFFSET));
                bod.createFixture(tl.createFix());
                bodies.add(bod);

                tileSpacing.put(tl.getPosition(), tileSpace(tl));
            }
        }
    }

    private TextureRegion pickTx(Integer texList) {
        String[] names = frTex.getTileList(texList);
        int txPick = (int)fct.random(0, names.length - 1);

        return frTex.getTileTex(names[txPick]);
    }

    private int isEDGE(tile tl) {
        if (tl.getPosition().x == 0) return -1;
        if (tl.getPosition().x == numPowerTILES) return 1;
        return 0;
    }

    private void getNeighbours(tile tl) {
        //array of neighbouring tile positions
        neighbours.clear();
        neighbours.put("RIGHT", new Vector2(tl.getPosition().x + 1, tl.getPosition().y));
        neighbours.put("LEFT", new Vector2(tl.getPosition().x - 1, tl.getPosition().y));
        neighbours.put("TOP", new Vector2(tl.getPosition().x, tl.getPosition().y + 1));
        neighbours.put("TOPLEFT", new Vector2(tl.getPosition().x - 1, tl.getPosition().y + 1));
        neighbours.put("TOPRIGHT", new Vector2(tl.getPosition().x + 1, tl.getPosition().y + 1));
    }

    private boolean hasNeighbours(tile tl) {
        if (isEDGE(tl) == 1 && tiles.containsKey(neighbours.get("LEFT")) && tiles.containsKey(neighbours.get("TOP"))) return true;
        if (isEDGE(tl) == -1 && tiles.containsKey(neighbours.get("RIGHT")) && tiles.containsKey(neighbours.get("TOP"))) return true;
        return tiles.containsKey(neighbours.get("LEFT")) && tiles.containsKey(neighbours.get("RIGHT")) && tiles.containsKey(neighbours.get("TOP"));
    }

    //1 represents one space available, 2 represents two, etc..
    private int tileSpace(tile tl){
        if (!tiles.containsKey(neighbours.get("TOP")) && isEDGE(tl) == 0) {
            if (tiles.containsKey(neighbours.get("LEFT")) && tiles.containsKey(neighbours.get("RIGHT")) && !tiles.containsKey(neighbours.get("TOPLEFT")) && !tiles.containsKey(neighbours.get("TOPRIGHT"))) return 3;
            if (tiles.containsKey(neighbours.get("RIGHT")) && !tiles.containsKey(neighbours.get("TOPRIGHT"))) return 2;
        }
        if (!tiles.containsKey(neighbours.get("TOP"))) return 1;
        return 0;
    }

    public static boolean inFrame(Vector2 pos, int size) {
        /*Check if tile is in viewport. OpenGL is good about only rendering things on screen,
        but draw calls are still very expensive, so only call draw if actually on screen. */
        bottomLeftViewpoint = play.getBottomLeftViewPoint();
        if (pos.x + size > bottomLeftViewpoint.x && pos.x - size < bottomLeftViewpoint.x + cWIDTH) {
            if (pos.y + size > bottomLeftViewpoint.y && pos.y - size < bottomLeftViewpoint.y + cHEIGHT) {
                return true;
            }
        }
        return false;
    }

    public void update(float delta) {
        forestHandlerBack.update(delta);
        forestHandlerFront.update(delta);
    }

    public void renderBack(SpriteBatch sb) {
        forestHandlerBack.renderBack(sb);
    }

    public void renderTiles(SpriteBatch sb) {
        for(tile tl : tiles.values()){
            if (inFrame(tl.getRelPOS(), TILESIZE)) {
                renderTile(sb, tl.getTX(), tl.getRelPOS(), tl.getFlip());
                if (tl.getGrassTX() != null) {
                    renderTile(sb, tl.getGrassTX(), tl.getRelPOS(), std);
                }
            }
        }
    }

    private void renderTile(SpriteBatch sb, TextureRegion tx, Vector2 pos, Vector2 flip) {
        sb.begin();
        sb.setColor(Color.WHITE);
        sb.draw(
                tx,
                pos.x,
                pos.y,
                8, 8, 16, 16, flip.x, flip.y, 0
        );
        sb.end();
    }

    public void renderFront(SpriteBatch sb) {
        forestHandlerFront.renderFront(sb);
    }

    public ConcurrentHashMap<Vector2, tile> getTileMap() {
        return tiles;
    }

    public int getOFFSET() {
        return OFFSET;
    }

    public ArrayList<Body> getBodies() {
        return bodies;
    }

    public int[] getHeightMap() {
        return heightMap;
    }
}
