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
        this(frames, 1 / 12f, 0);
    }

    public animate(TextureRegion[] frames, float delay, int animOffset) {
        setFrames(frames, delay, animOffset);
    }

    public void setFrames(TextureRegion[] frames, float delay, int animOffset) {
        this.frames = frames;
        this.delay = delay;
        t = 0;
        cFrame = animOffset;
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
        return frames[cFrame];
    }

    public int getLoopsNum() { return loops; }
}
