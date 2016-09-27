package tonegod.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import tonegod.emitter.particle.ParticleData;

/**
 * @author t0neg0d
 */
public class SpriteInfluencer implements ParticleInfluencer {
    private boolean enabled = true;
    private boolean useRandomImage = false;
    private boolean animate = true;
    private int totalFrames = -1;
    private float fixedDuration = 0f;
    private boolean cycle = false;
    private transient float targetInterval;
    private int[] frameSequence = null;

    @NotNull
    @Override
    public String getName() {
        return "Sprite influencer";
    }

    @Override
    public void update(@NotNull ParticleData particleData, float tpf) {
        if (enabled) {
            if (animate) {
                particleData.spriteInterval += tpf;
                targetInterval = (cycle) ? fixedDuration : particleData.spriteDuration;
                if (particleData.spriteInterval >= targetInterval) {
                    updateFrame(particleData);
                }
            }
        }
    }

    private void updateFrame(ParticleData p) {
        if (frameSequence == null) {
            p.spriteCol++;
            if (p.spriteCol == p.emitterNode.getSpriteColCount()) {
                p.spriteCol = 0;
                p.spriteRow++;
                if (p.spriteRow == p.emitterNode.getSpriteRowCount())
                    p.spriteRow = 0;
            }
        } else {
            p.spriteIndex++;
            if (p.spriteIndex == frameSequence.length)
                p.spriteIndex = 0;
            p.spriteRow = (int) FastMath.floor(frameSequence[p.spriteIndex] / p.emitterNode.getSpriteRowCount()) - 2;
            p.spriteCol = (int) frameSequence[p.spriteIndex] % p.emitterNode.getSpriteColCount();
        }
        p.spriteInterval -= targetInterval;
    }

    @Override
    public void initialize(@NotNull ParticleData particleData) {
        if (totalFrames == -1) {
            totalFrames = particleData.emitterNode.getSpriteColCount() * particleData.emitterNode.getSpriteRowCount();
            if (totalFrames == 1) setAnimate(false);
        }
        if (useRandomImage) {
            if (frameSequence == null) {
                particleData.spriteIndex = FastMath.nextRandomInt(0, totalFrames - 1);
                particleData.spriteRow = (int) FastMath.floor(particleData.spriteIndex / particleData.emitterNode.getSpriteRowCount()) - 1;
                particleData.spriteCol = (int) particleData.spriteIndex % particleData.emitterNode.getSpriteColCount();
                //	p.spriteCol = FastMath.nextRandomInt(0,frameSequence.length-1);
                //	p.spriteRow = FastMath.nextRandomInt(0,frameSequence.length-1);
            } else {
                particleData.spriteIndex = FastMath.nextRandomInt(0, frameSequence.length - 1);
                particleData.spriteRow = (int) FastMath.floor(frameSequence[particleData.spriteIndex] / particleData.emitterNode.getSpriteRowCount()) - 1;
                particleData.spriteCol = (int) frameSequence[particleData.spriteIndex] % particleData.emitterNode.getSpriteColCount();
            }
        } else {
            if (frameSequence != null) {
                particleData.spriteIndex = frameSequence[0];
                particleData.spriteRow = (int) FastMath.floor(frameSequence[particleData.spriteIndex] / particleData.emitterNode.getSpriteRowCount()) - 2;
                particleData.spriteCol = (int) frameSequence[particleData.spriteIndex] % particleData.emitterNode.getSpriteColCount();
            } else {
                particleData.spriteIndex = 0;
                particleData.spriteRow = 0;
                particleData.spriteCol = 0;
            }
        }
        if (animate) {
            particleData.spriteInterval = 0;
            if (!cycle) {
                if (frameSequence == null)
                    particleData.spriteDuration = particleData.startlife / (float) totalFrames;
                else
                    particleData.spriteDuration = particleData.startlife / (float) frameSequence.length;
            }
        }
    }

    @Override
    public void reset(@NotNull ParticleData particleData) {
        particleData.spriteIndex = 0;
        particleData.spriteCol = 0;
        particleData.spriteRow = 0;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Particles will/will not use sprite animations
     *
     * @param animate boolean
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    /**
     * Current animation state of particle
     *
     * @return Returns if particles use sprite animation
     */
    public boolean getAnimate() {
        return this.animate;
    }

    /**
     * Sets if particles should select a random start image from the provided sprite texture
     *
     * @param useRandomImage boolean
     */
    public void setUseRandomStartImage(boolean useRandomImage) {
        this.useRandomImage = useRandomImage;
    }

    /**
     * Returns if particles currently select a random start image from the provided sprite texture
     *
     * @param useRandomImage boolean
     */
    public boolean getUseRandomStartImage() {
        return this.useRandomImage;
    }

    public void setFrameSequence(int... frame) {
        frameSequence = frame;
    }

    public int[] getFrameSequence() {
        return this.frameSequence;
    }

    public void clearFrameSequence() {
        frameSequence = null;
    }

    /**
     * Animated texture should cycle and use the provided duration between frames (0 diables
     * cycling)
     *
     * @param fixedDuration duration between frame updates
     */
    public void setFixedDuration(float fixedDuration) {
        if (fixedDuration != 0) {
            this.cycle = true;
            this.fixedDuration = fixedDuration;
        } else {
            this.cycle = false;
            this.fixedDuration = 0;
        }
    }

    /**
     * Returns the current duration used between frames for cycled animation
     */
    public float getFixedDuration() {
        return this.fixedDuration;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        /*
        private boolean enabled = true;
		private boolean useRandomImage = false;
		private boolean animate = true;
		private float fixedDuration = 0f;
		private int[] frameSequence = null;
		 */
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(useRandomImage, "useRandomImage", false);
        oc.write(animate, "animate", true);
        oc.write(fixedDuration, "fixedDuration", 0f);
        oc.write(enabled, "enabled", true);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        useRandomImage = ic.readBoolean("useRandomImage", false);
        animate = ic.readBoolean("animate", true);
        fixedDuration = ic.readFloat("fixedDuration", 0f);
        enabled = ic.readBoolean("enabled", true);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            SpriteInfluencer clone = (SpriteInfluencer) super.clone();
            clone.setAnimate(animate);
            clone.setFixedDuration(fixedDuration);
            clone.setUseRandomStartImage(useRandomImage);
            clone.setFrameSequence(frameSequence);
            clone.setEnabled(enabled);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
