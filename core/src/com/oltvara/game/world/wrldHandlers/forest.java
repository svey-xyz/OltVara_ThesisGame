package com.oltvara.game.world.wrldHandlers;

import static com.oltvara.game.mainGame.fct;
import static com.oltvara.game.mainGame.frTex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.oltvara.game.mainGame;
import com.oltvara.game.world.bush;
import com.oltvara.game.world.rock;
import com.oltvara.game.world.tree;

import java.util.*;

public class forest {

    private ArrayList<tree> trees;
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

    forest(int offset,  HashMap<Vector2, Integer> tileSpacing, int[] heightMap) {
        this.offset = offset;
        this.tileSpaces = tileSpacing;
        this.heightMap = heightMap;

        trees = new ArrayList<>();
        bushes = new ArrayList<>();
        rocks = new ArrayList<>();

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
                        rockNames = frTex.getRockList(frTex.SMALLROCK);
                        mossNames = frTex.getRockList(frTex.SMALLROCKMOSS);
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
                        bushes.add(new bush(pos, offset, frTex.SMALLBUSH));
                    } else if (rand > 0.4 && rand < 0.5) {
                        bushes.add(new bush(pos, offset, frTex.MEDIUMBUSH));
                    }
                }

                //place objects that fit within three tiles
                //place large objects first to give them more of a chance to spawn
                if (tileSpaces.get(pos) == 3) {
                    if (fct.distance(prevPos, pos) > MINDISTMED) {
                        if (rand < 0.15) {
                            trees.add(new tree(pos, offset, frTex.MEDIUMBUSHYTREE));
                            prevPos = pos;
                        } else if (rand < 0.2) {
                            trees.add(new tree(pos, offset, frTex.MEDIUMSPIKYTREE));
                            prevPos = pos;
                        } else if (rand < 0.21) {
                            trees.add(new tree(pos, offset, frTex.COLOURFULTREE));
                            prevPos = pos;
                        }
                    }
                }

                //place objects that fit within two tiles
                if (tileSpaces.get(pos) >= 2) {
                    if (rand < 0.2) {
                        bushes.add(new bush(pos, offset, frTex.LARGEBUSH));
                    }

                    if (fct.distance(prevPos, pos) > MINDISTSMALL) {
                        if (rand < 0.1) {
                            trees.add(new tree(pos, offset, frTex.SMALLSPIKYTREE));
                            prevPos = pos;
                        } else if (rand < 0.15) {
                            trees.add(new tree(pos, offset, frTex.SMALLBUSHYTREE));
                            prevPos = pos;
                        }
                    }
                }
            }
        }
    }

    //Rendering layers
    //Use the inFrame function to make render calls only for object on screen
    void renderFront(SpriteBatch sb) {
        for (int i = rocks.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(rocks.get(i).getRenPOS(), rocks.get(i).getSize())) {
                renderRock(sb, rocks.get(i).getTX(), rocks.get(i).getRenPOS(), Color.WHITE);
                if (rocks.get(i).getMossTX() != null) renderRock(sb, rocks.get(i).getMossTX(), rocks.get(i).getRenPOS(), rocks.get(i).getMossCol());
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

    void renderBack(SpriteBatch sb) {
        for (int i = trees.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(trees.get(i).getRenPOS(), trees.get(i).getSize())) {
                trees.get(i).render(sb);
            }
        }
        for (int i = bushes.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(bushes.get(i).getRenPOS(), bushes.get(i).getSize())) {
                bushes.get(i).render(sb);
            }
        }
        for (int i = rocks.size() - 1; i >= 0; i--) {
            if (chunk.inFrame(rocks.get(i).getRenPOS(), rocks.get(i).getSize())) {
                renderRock(sb, rocks.get(i).getTX(), rocks.get(i).getRenPOS(), Color.WHITE);
                if (rocks.get(i).getMossTX() != null) renderRock(sb, rocks.get(i).getMossTX(), rocks.get(i).getRenPOS(), rocks.get(i).getMossCol());
            }
        }
    }

    private void renderRock(SpriteBatch sb, TextureRegion tx, Vector2 pos, Color col) {
        sb.begin();
        sb.setColor(col);
        sb.draw(tx, pos.x, pos.y);
        sb.end();
    }

    public void update(float delta) {
        for (int i = bushes.size() - 1; i >= 0; i--) {
            bushes.get(i).update(delta);
        }
        for (int i = trees.size() - 1; i >= 0; i--) {
            trees.get(i).update(delta);
        }
    }

}
