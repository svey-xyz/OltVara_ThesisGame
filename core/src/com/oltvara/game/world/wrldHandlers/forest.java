package com.oltvara.game.world.wrldHandlers;

import static com.oltvara.game.mainGame.fct;
import static com.oltvara.game.mainGame.frTex;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.oltvara.game.mainGame;
import com.oltvara.game.world.bush;
import com.oltvara.game.world.tree;

import java.util.*;

public class forest {

    private ArrayList<tree> trees;
    private ArrayList<bush> bushes;
    private HashMap<Vector2, Integer> tileSpaces;
    private int[] heightMap;

    private int offset;

    private final float MINDISTSMALL = 3f;
    private final float MINDISTMED = 5f;

    forest(int offset,  HashMap<Vector2, Integer> tileSpacing, int[] heightMap) {
        this.offset = offset;
        this.tileSpaces = tileSpacing;
        this.heightMap = heightMap;

        trees = new ArrayList<>();
        bushes = new ArrayList<>();

        createTrees();
        Collections.shuffle(trees);

        Collections.shuffle(bushes);
    }

    private void createTrees() {
        float rand;
        Vector2 pos;
        Vector2 prevPos = new Vector2(-10, -10);

        //Sort through the tiles from left to right to be able to do distance calc without having to iterate twice, and avoiding concurrent modification
        for (int x=0; x<mainGame.numTILES; x++) {
            pos = new Vector2(x, heightMap[x]);
            //System.out.println("Pos: " + pos + ", treeVal: " + treesPos.get(pos));
            if (tileSpaces.get(pos) != null) {
                rand = (float) fct.random();

                if (tileSpaces.get(pos) > 0) {
                    if (rand < 0.5) {
                        bushes.add(new bush(pos, offset, frTex.MEDIUMBUSH));
                    }
                }

                if (tileSpaces.get(pos) == 2) {
                    if (fct.distance(prevPos, pos) > MINDISTSMALL) {
                        if (rand < 0.2) {
                            trees.add(new tree(pos, offset, frTex.SMALLBUSHYTREE));
                            prevPos = pos;
                        } else if (rand < 0.3) {
                            trees.add(new tree(pos, offset, frTex.SMALLSPIKYTREE));
                        }
                    }
                }

                if (tileSpaces.get(pos) == 3) {
                    if (fct.distance(prevPos, pos) > MINDISTMED) {
                        if (rand < 0.15) {
                            trees.add(new tree(pos, offset, frTex.MEDIUMBUSHYTREE));
                            prevPos = pos;
                        } else if (rand < 0.2) {
                            trees.add(new tree(pos, offset, frTex.MEDIUMSPIKYTREE));
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
