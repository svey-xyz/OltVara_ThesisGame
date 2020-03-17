package com.oltvara.game.handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class animate {

    private TextureRegion[] frames;
    private float t;
    private float delay;
    private int cFrame;
    private int loops;

    public animate() {}

    public animate(TextureRegion[] frames) {
        this(frames, 1 / 12f);
    }

    public animate(TextureRegion[] frames, float delay) {
        setFrames(frames, delay);
    }

    public void setFrames(TextureRegion[] frames, float delay) {
        this.frames = frames;
        this.delay = delay;
        t = 0;
        cFrame = 0;
        loops = 0;
    }

    public void update(float dt) {
        if (delay <= 0) return;

        t+= dt;
        while (t >= delay) {
            step();
        }
    }

    private void step() {
        t -= delay;
        cFrame++;
        if (cFrame == frames.length) {
            cFrame = 0;
            loops++;
        }
    }

    public TextureRegion getFrame() {
        if (frames[cFrame] == null) System.out.println("it's not working");
        return frames[cFrame]; }

    public int getLoopsNum() { return loops; }
}
