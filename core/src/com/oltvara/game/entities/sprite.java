package com.oltvara.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.oltvara.game.handlers.animate;
import com.oltvara.game.handlers.physicsVars;

public class sprite {

    protected Body body;
    protected Vector2 pos;
    protected Vector2 offset = new Vector2(0, 0);
    protected animate anim;

    protected float width, height;

    public sprite(Body body, Vector2 offset) {
        this.body = body;
        this.pos = body.getPosition();
        this.offset = offset;
        anim = new animate();
    }

    public sprite(Body body) {
        this.body = body;
        this.pos = body.getPosition();
        anim = new animate();
    }

    public sprite(Vector2 pos) {
        this.pos = pos;
        anim = new animate();
    }

    protected void setAnim(TextureRegion[] texReg, float delay, int animOffset) {
        anim.setFrames(texReg, delay, animOffset);
        width = texReg[0].getRegionWidth();
        height = texReg[0].getRegionHeight();
    }

    public void update(float dt) {
        if (body != null) {
            pos = body.getPosition();
        }
        anim.update(dt);
    }

    public void render(SpriteBatch sb, Color tint) {
        sb.setColor(tint);
        sb.draw(
                anim.getFrame(),
                (pos.x * physicsVars.PPM - width / 2 + offset.x),
                (pos.y * physicsVars.PPM - height / 2 + offset.y)
        );
    }

    public Body getBod() { return body; }
    public Vector2 getPosition() { return pos; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getLoopsNum() { return anim.getLoopsNum(); }
    public void setLoopsNum(int loops) { anim.setLoops(loops); }

}
