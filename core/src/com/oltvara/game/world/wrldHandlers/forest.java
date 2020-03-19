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
    private HashMap<Vector2, Integer> treesPos;
    private ArrayList<Vector2> bushPos;
    private int[] heightMap;

    private int offset;

    private final float MINDISTSMALL = 3f;
    private final float MINDISTMED = 5f;

    forest(int offset, ArrayList<Vector2> bushPos, HashMap<Vector2, Integer> treesPos, int[] heightMap) {
        this.offset = offset;
        this.bushPos = bushPos;
        this.treesPos = treesPos;
        this.heightMap = heightMap;

        trees = new ArrayList<>();
        bushes = new ArrayList<>();

        createTrees();
        Collections.shuffle(trees);

        createBushes();
        Collections.shuffle(bushes);
    }

    private void createTrees() {
        float rand;
        Vector2 pos;
        Vector2 prevPos = new Vector2(-10, -10);

        //Sort through the tiles from left to right to be able to do distance calc without having to iterate twice, and avoiding concurrent modification
        for (int x=0; x<mainGame.numTILES; x++) {
            pos = new Vector2(x, heightMap[x]);
            if (treesPos.get(pos) == 1) {
                rand = (float)fct.random();
                if (fct.distance(prevPos, pos) > MINDISTSMALL) {
                    if (rand < 0.2) {
                        trees.add(new tree(pos, offset, frTex.SMALLBUSHYTREE));
                        prevPos = pos;
                    } else if (rand < 0.3) {
                        trees.add(new tree(pos, offset, frTex.SMALLSPIKYTREE));
                    }
                }
            }

            if (treesPos.get(pos) == 2) {
                rand = (float)fct.random();
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

    private void createBushes() {
        float rand;
        for (Vector2 pos : bushPos) {
            rand = (float)fct.random();
            if (rand < 0.5) {
                bushes.add(new bush(pos, offset, frTex.MEDIUMBUSH));
            }
        }
    }

    public void renderFront(SpriteBatch sb) {
        for (bush bs : bushes) {
            bs.render(sb);
        }
        for (tree tr : trees) {
            tr.render(sb);
        }
    }

    public void renderBack(SpriteBatch sb) {
        for (tree tr : trees) {
            tr.render(sb);
        }
        for (bush bs : bushes) {
            bs.render(sb);
        }
    }

    public void update(float delta) {
        for (tree tr : trees) {
            tr.update(delta);
        }
        for (bush bs : bushes) {
            bs.update(delta);
        }
    }

}
