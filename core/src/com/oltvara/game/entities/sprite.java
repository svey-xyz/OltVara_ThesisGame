package com.oltvara.game.entities;
import static com.oltvara.game.mainGame.fct;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.oltvara.game.handlers.animate;
import com.oltvara.game.world.wrldHandlers.physicsVars;

public class sprite {

    protected Body body;
    protected animate anim;

    protected float width, height;

    public sprite(Body body) {
        this.body = body;
        anim = new animate();
    }

    public void setAnim(TextureRegion[] texReg, float delay) {
        anim.setFrames(texReg, delay);
        width = texReg[0].getRegionWidth();
        height = texReg[0].getRegionHeight();
    }

    public void update(float dt) {
        anim.update(dt);
    }

    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(
                anim.getFrame(),
                (body.getPosition().x * physicsVars.PPM - width / 2),
                (body.getPosition().y * physicsVars.PPM - height / 2)
        );
        sb.end();
    }

    public Body getBod() { return body; }
    public Vector2 getPosition() { return body.getPosition(); }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

}
