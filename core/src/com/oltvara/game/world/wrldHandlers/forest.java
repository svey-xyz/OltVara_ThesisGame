package com.oltvara.game.world.wrldHandlers;

import static com.oltvara.game.mainGame.fct;
import static com.oltvara.game.mainGame.frTex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.mainGame;
import com.oltvara.game.world.bush;
import com.oltvara.game.world.rock;
import com.oltvara.game.world.tree;
import com.oltvara.game.world.wellObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static com.oltvara.game.handlers.texture.texTypesNames.*;

public class forest {

    private ArrayList<tree> trees;
    private ArrayList<wellObj> wells;
    private ArrayList<bush> bushes;
    private ArrayList<rock> rocks;
    private HashMap<Vector2, Integer> tileSpaces;
    private int[] heightMap;

    private String[] rockNames, mossNames;
    private int txPick;
    TextureRegion moss;

    private int offset;

    private final float MINDISTSMALL = 3f;
    private final float MINDISTMED = 5f;

    private ArrayList<Body> bodies;

    forest(int offset,  HashMap<Vector2, Integer> tileSpacing, int[] heightMap) {
        this.offset = offset;
        this.tileSpaces = tileSpacing;
        this.heightMap = heightMap;

        trees = new ArrayList<>();
        bushes = new ArrayList<>();
        rocks = new ArrayList<>();
        wells= new ArrayList<>();

        bodies = new ArrayList<>();

        createPlants();

        Collections.shuffle(trees);
        Collections.shuffle(bushes);
    }

    private void createPlants() {
        float rand;
        Vector2 pos;
        Vector2 prevPos = new Vector2(-10, -10);

        //Sort through the tiles from left to right to be able to do distance calc without having to iterate twice, and avoiding concurrent modification
        for (int x=0; x<mainGame.numTILES; x++) {
            pos = new Vector2(x, heightMap[x]);
            //System.out.println("Pos: " + pos + ", treeVal: " + treesPos.get(pos));
            if (tileSpaces.get(pos) != null) {
                rand = (float) fct.random();

                //place objects that fit within one tile
                if (tileSpaces.get(pos) > 0) {
                    //Rock placement
                    if (rand < 0.2) {
                        rockNames = frTex.getRockList(SMALLROCK);
                        mossNames = frTex.getRockList(SMALLROCKMOSS);
                        txPick = (int)fct.random(0, rockNames.length - 1);
                        moss = null;

                        //create moss overlay on some rocks
                        if (rand < 0.15) moss = frTex.getRockTex(mossNames[txPick]);

                        //moss overlay has random colour distribution same as trees
                        rocks.add(new rock(pos, offset, frTex.getRockTex(rockNames[txPick]), moss, fct.gaussianCol(frTex.getMossCol(), frTex.getMossSDCol())));
                    }

                    //places small and medium bushes with probability
                    //rand > 0.1 to allow for spaces with only rocks and no bushes
                    if (rand > 0.1 && rand < 0.4) {
                        bushes.add(new bush(frTex.getBushType(SMALLBUSH), pos, offset));
                    } else if (rand > 0.4 && rand < 0.5) {
                        bushes.add(new bush(frTex.getBushType(MEDIUMBUSH), pos, offset));
                    }
                }

                //place objects that fit within three tiles
                //place large objects first to give them more of a chance to spawn
                if (tileSpaces.get(pos) == 3) {
                    if (fct.distance(prevPos, pos) > MINDISTMED) {
                        if (rand < 0.15) {
                            trees.add(new tree(frTex.getTreeType(MEDIUMBUSHYTREE), pos, offset));
                            prevPos = pos;
                        } else if (rand < 0.2) {
                            trees.add(new tree(frTex.getTreeType(MEDIUMSPIKYTREE), pos, offset));
                            prevPos = pos;
                        } else if (rand < 0.21) {
                            trees.add(new tree(frTex.getTreeType(COLOURFULTREE), pos, offset));
                            prevPos = pos;
                        } else if (rand < 0.225) {
                            TextureRegion moss = new Random().nextBoolean() ? frTex.getWellTX("wellMoss") : null;
                            TextureRegion rocks = new Random().nextBoolean() ? frTex.getWellTX("wellRocks") : null;
                            TextureRegion rocksMoss = null;
                            if (rocks != null) rocksMoss = new Random().nextBoolean() ? frTex.getWellTX("wellRocksMoss") : null;

                            wellObj well = new wellObj(pos, offset, frTex.getWellTX("well"), moss, rocks, rocksMoss, fct.gaussianCol(frTex.getMossCol(), frTex.getMossSDCol()), fct.gaussianCol(frTex.getMossCol(), frTex.getMossSDCol()));
                            wells.add(well);

                            Body bod = play.boxWorld.createBody(well.createBodDef(offset));
                            bod.createFixture(well.createFix());
                            bodies.add(bod);
                        }
                    }
                }

                //place objects that fit within two tiles
                if (tileSpaces.get(pos) >= 2) {
                    if (rand < 0.2) {
                        bushes.add(new bush(frTex.getBushType(LARGEBUSH), pos, offset));
                    }

                    if (fct.distance(prevPos, pos) > MINDISTSMALL) {
                        if (rand < 0.1) {
                            trees.add(new tree(frTex.getTreeType(SMALLSPIKYTREE), pos, offset));
                            prevPos = pos;
                        } else if (rand < 0.15) {
                            trees.add(new tree(frTex.getTreeType(SMALLBUSHYTREE), pos, offset));
                            prevPos = pos;
                        }
                    }
                }
            }
        }
    }

    //Rendering layers
    //Use the inFrame function to make render calls only for object on screen
    void renderFrontTrees(SpriteBatch sb) {
        for (int i = trees.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(trees.get(i).getRenPOS(), trees.get(i).getSize())) {
                trees.get(i).render(sb);
            }
        }
    }

    void renderFrontFoliage(SpriteBatch sb) {
        for (int i = bushes.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(bushes.get(i).getRenPOS(), bushes.get(i).getSize())) {
                bushes.get(i).render(sb);
            }
        }
        for (int i = rocks.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(rocks.get(i).getRenPOS(), rocks.get(i).getSize())) {
                if (rocks.get(i).getMossTX() != null) renderObj(sb, rocks.get(i).getMossTX(), rocks.get(i).getRenPOS(), rocks.get(i).getMossCol());
                renderObj(sb, rocks.get(i).getTX(), rocks.get(i).getRenPOS(), Color.WHITE);
            }
        }
    }

    void renderBack(SpriteBatch sb) {
        for (int i = rocks.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(rocks.get(i).getRenPOS(), rocks.get(i).getSize())) {
                if (rocks.get(i).getMossTX() != null) renderObj(sb, rocks.get(i).getMossTX(), rocks.get(i).getRenPOS(), rocks.get(i).getMossCol());
                renderObj(sb, rocks.get(i).getTX(), rocks.get(i).getRenPOS(), Color.WHITE);
            }
        }
        for (int i = bushes.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(bushes.get(i).getRenPOS(), bushes.get(i).getSize())) {
                bushes.get(i).render(sb);
            }
        }
        for (int i = trees.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(trees.get(i).getRenPOS(), trees.get(i).getSize())) {
                trees.get(i).render(sb);
            }
        }
    }

    void renderMain(SpriteBatch sb) {
        for (int i = wells.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(wells.get(i).getRenPOS(), wells.get(i).getSize())) {

                if (wells.get(i).getMossTX() != null) renderObj(sb, wells.get(i).getMossTX(), wells.get(i).getRenPOS(), wells.get(i).getMossCol());
                if (wells.get(i).getRockMossTX() != null) renderObj(sb, wells.get(i).getRockMossTX(), wells.get(i).getRenPOS(), wells.get(i).getRockMossCol());
                if (wells.get(i).getRockTX() != null) renderObj(sb, wells.get(i).getRockTX(), wells.get(i).getRenPOS(), Color.WHITE);
                renderObj(sb, wells.get(i).getTX(), wells.get(i).getRenPOS(), Color.WHITE);
            }
        }
    }

    private void renderObj(SpriteBatch sb, TextureRegion tx, Vector2 pos, Color col) {
        sb.setColor(col);
        sb.draw(tx, pos.x, pos.y);
    }

    public void update(float delta) {
        for (int i = bushes.size() - 1; i >= 0; i--) {
            bushes.get(i).update(delta);
        }
        for (int i = trees.size() - 1; i >= 0; i--) {
            trees.get(i).update(delta);
        }
    }

    public ArrayList<Body> getBodies() { return bodies; }

}
