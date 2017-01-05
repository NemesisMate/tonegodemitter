package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.UnsafeArray;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;

/**
 * @author t0neg0d
 * @edit JavaSaBr
 */
public final class RotationInfluencer extends AbstractInterpolatedParticleInfluencer {

    /**
     * The list of speeds.
     */
    private UnsafeArray<Vector3f> speeds;

    /**
     * The speed factor.
     */
    private final Vector3f speedFactor;

    /**
     * The flag of using random direction.
     */
    private boolean randomDirection;

    /**
     * The flag of using random speed.
     */
    private boolean randomSpeed;

    /**
     * The direction.
     */
    private boolean direction;

    /**
     * The flag of using random start rotation for X.
     */
    private boolean randomStartRotationX;

    /**
     * The flag of using random start rotation for Y.
     */
    private boolean randomStartRotationY;

    /**
     * The flag of using random start rotation for Z.
     */
    private boolean randomStartRotationZ;

    public RotationInfluencer() {
        this.speeds = ArrayFactory.newUnsafeArray(Vector3f.class);
        this.speedFactor = Vector3f.ZERO.clone();
        this.randomDirection = true;
        this.randomSpeed = true;
        this.direction = true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Rotation influencer";
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        if (speeds.size() > 1) {

            if (particleData.rotationIndex >= speeds.size()) {
                particleData.rotationIndex = 0;
            }

            particleData.rotationInterval += tpf;

            if (particleData.rotationInterval >= particleData.rotationDuration) {
                updateRotation(particleData);
            }

            blend = particleData.rotationInterpolation.apply(particleData.rotationInterval / particleData.rotationDuration);

            particleData.rotationSpeed.interpolateLocal(particleData.startRotationSpeed, particleData.endRotationSpeed, blend);
        }

        particleData.angles.addLocal(particleData.rotationSpeed.mult(tpf));

        super.updateImpl(particleData, tpf);
    }

    /**
     * Update a rotation.
     *
     * @param particleData the particle data.
     */
    private void updateRotation(@NotNull final ParticleData particleData) {
        particleData.rotationIndex++;

        if (!isCycle()) {
            if (particleData.rotationIndex == speeds.size() - 1) {
                particleData.rotationIndex = 0;
            }
        } else {
            if (particleData.rotationIndex == speeds.size()) {
                particleData.rotationIndex = 0;
            }
        }

        nextRotationSpeed(particleData, particleData.rotationIndex, particleData.startRotationSpeed);

        int index = particleData.rotationIndex + 1;

        if (index == speeds.size()) {
            index = 0;
        }

        nextRotationSpeed(particleData, index, particleData.endRotationSpeed);

        final Array<Interpolation> interpolations = getInterpolations();
        particleData.rotationInterpolation = interpolations.get(particleData.rotationIndex);
        particleData.rotationInterval -= particleData.rotationDuration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {

        if (speeds.isEmpty()) {
            addRotationSpeed(new Vector3f(0, 0, 10));
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData) {

        particleData.rotationIndex = 0;
        particleData.rotationInterval = 0f;
        particleData.rotationDuration = isCycle() ? getFixedDuration() : particleData.startlife / ((float) speeds.size() - 1);

        if (isRandomDirection()) {
            particleData.rotateDirectionX = FastMath.rand.nextBoolean();
            particleData.rotateDirectionY = FastMath.rand.nextBoolean();
            particleData.rotateDirectionZ = FastMath.rand.nextBoolean();
        }

        nextRotationSpeed(particleData, particleData.rotationIndex, particleData.startRotationSpeed);

        particleData.rotationSpeed.set(particleData.startRotationSpeed);

        if (speeds.size() > 1) {
            nextRotationSpeed(particleData, particleData.rotationIndex + 1, particleData.endRotationSpeed);
        }

        final Array<Interpolation> interpolations = getInterpolations();
        particleData.rotationInterpolation = interpolations.get(particleData.rotationIndex);

        if (isRandomStartRotationX() || isRandomStartRotationY() || isRandomStartRotationZ()) {
            calculateRandomAngles(particleData);
        } else {
            particleData.angles.set(0, 0, 0);
        }

        super.initializeImpl(particleData);
    }

    /**
     * Calculate random angles.
     *
     * @param particleData the particle data.
     */
    private void calculateRandomAngles(final @NotNull ParticleData particleData) {

        final float x = randomStartRotationX ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0;
        final float y = randomStartRotationY ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0;
        final float z = randomStartRotationZ ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0;

        particleData.angles.set(x, y, z);
    }

    /**
     * Calculate next rotation speed.
     *
     * @param particleData the particle data.
     * @param index        the index.
     * @param store        the store vector.
     */
    private void nextRotationSpeed(@NotNull final ParticleData particleData, final int index, final Vector3f store) {
        store.set(speeds.get(index));

        if (isRandomSpeed()) {
            store.set(FastMath.nextRandomFloat() * store.x, FastMath.nextRandomFloat() * store.y,
                    FastMath.nextRandomFloat() * store.z);
        }

        if (isRandomDirection()) {
            store.x = particleData.rotateDirectionX ? store.x : -store.x;
            store.y = particleData.rotateDirectionY ? store.y : -store.y;
            store.z = particleData.rotateDirectionZ ? store.z : -store.z;
        }
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.angles.set(0, 0, 0);
        super.reset(particleData);
    }

    /**
     * Add rotation speed.
     *
     * @param speed the new rotation speed.
     */
    public void addRotationSpeed(@NotNull final Vector3f speed) {
        addRotationSpeed(speed, Interpolation.LINEAR);
    }

    /**
     * Add rotation speed.
     *
     * @param speed         the new rotation speed.
     * @param interpolation the interpolation.
     */
    public void addRotationSpeed(@NotNull final Vector3f speed, @NotNull final Interpolation interpolation) {
        addInterpolation(interpolation);
        speeds.add(speed.clone());
    }

    /**
     * Remove rotation speed and interpolation for the index.
     *
     * @param index the index.
     */
    public void removeRotationSpeed(final int index) {
        removeInterpolation(index);
        speeds.slowRemove(index);
    }

    /**
     * Remove all rotation speeds.
     */
    public void removeAll() {
        clearInterpolations();
        this.speeds.clear();
    }

    /**
     * @return the list of rotations.
     */
    public Array<Vector3f> getRotationSpeeds() {
        return speeds;
    }

    /**
     * @param index the index.
     * @return the rotation speed for the index.
     */
    @NotNull
    public Vector3f getRotationSpeed(final int index) {
        return speeds.get(index);
    }

    /**
     * Change a rotation speed for the index.
     *
     * @param rotationSpeed the new rotation speed.
     * @param index         the index.
     */
    public void updateRotationSpeed(final @NotNull Vector3f rotationSpeed, final int index) {
        speeds.set(index, rotationSpeed);
    }

    /**
     * Remove last a rotation speed and interpolation.
     */
    public void removeLast() {

        final Array<Vector3f> speeds = getRotationSpeeds();
        if (speeds.isEmpty()) return;

        final int index = speeds.size() - 1;

        removeInterpolation(index);
        speeds.fastRemove(index);
    }

    /**
     * Allows the influencer to choose a random rotation direction per axis as the particle is emitted.
     */
    public void setRandomDirection(final boolean randomDirection) {
        this.randomDirection = randomDirection;
    }

    /**
     * Returns if the influencer currently selects a random rotation direction per axis as the particle is emitted.
     */
    public boolean isRandomDirection() {
        return randomDirection;
    }

    /**
     * Allows the influencer to select a random rotation speed from 0 to the provided maximum speeds per axis
     */
    public void setRandomSpeed(final boolean randomSpeed) {
        this.randomSpeed = randomSpeed;
    }

    /**
     * Returns if the influencer currently to selects random rotation speeds per axis
     */
    public boolean isRandomSpeed() {
        return randomSpeed;
    }

    /**
     * @param randomStartRotationX the flag of using random start rotation for X.
     */
    public void setRandomStartRotationX(final boolean randomStartRotationX) {
        this.randomStartRotationX = randomStartRotationX;
    }

    /**
     * @param randomStartRotationY the flag of using random start rotation for Y.
     */
    public void setRandomStartRotationY(final boolean randomStartRotationY) {
        this.randomStartRotationY = randomStartRotationY;
    }

    /**
     * @param randomStartRotationZ the flag of using random start rotation for Z.
     */
    public void setRandomStartRotationZ(final boolean randomStartRotationZ) {
        this.randomStartRotationZ = randomStartRotationZ;
    }

    /**
     * @return true if this using random start rotation X.
     */
    public boolean isRandomStartRotationX() {
        return randomStartRotationX;
    }

    /**
     * @return true if this using random start rotation Y.
     */
    public boolean isRandomStartRotationY() {
        return randomStartRotationY;
    }

    /**
     * @return true if this using random start rotation Z.
     */
    public boolean isRandomStartRotationZ() {
        return randomStartRotationZ;
    }

    /**
     * Forces the rotation direction to always remain constant per particle
     *
     * @param direction boolean
     */
    public void setDirection(final boolean direction) {
        this.direction = direction;
    }

    /**
     * Returns if the rotation direction will always remain constant per particle
     */
    public boolean isDirection() {
        return direction;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(speeds.toArray(new Vector3f[speeds.size()]), "speeds", null);
        capsule.write(speedFactor, "speedFactor", Vector3f.ZERO);
        capsule.write(randomDirection, "randomDirection", true);
        capsule.write(randomSpeed, "randomSpeed", true);
        capsule.write(direction, "direction", true);
        capsule.write(randomStartRotationX, "randomStartRotationX", false);
        capsule.write(randomStartRotationY, "randomStartRotationY", false);
        capsule.write(randomStartRotationZ, "randomStartRotationZ", false);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);

        ArrayUtils.forEach(capsule.readSavableArray("speeds", null), speeds,
                (savable, toStore) -> toStore.add((Vector3f) savable));

        speedFactor.set((Vector3f) capsule.readSavable("speedFactor", Vector3f.ZERO.clone()));
        randomDirection = capsule.readBoolean("randomDirection", true);
        randomSpeed = capsule.readBoolean("randomSpeed", true);
        direction = capsule.readBoolean("direction", true);
        randomStartRotationX = capsule.readBoolean("randomStartRotationX", false);
        randomStartRotationY = capsule.readBoolean("randomStartRotationY", false);
        randomStartRotationZ = capsule.readBoolean("randomStartRotationZ", false);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final RotationInfluencer clone = (RotationInfluencer) super.clone();
        clone.speeds = ArrayFactory.newUnsafeArray(Vector3f.class);
        clone.speeds.addAll(speeds);
        clone.setDirection(direction);
        clone.setRandomDirection(randomDirection);
        clone.setRandomSpeed(randomSpeed);
        clone.randomStartRotationX = randomStartRotationX;
        clone.randomStartRotationY = randomStartRotationY;
        clone.randomStartRotationZ = randomStartRotationZ;
        return clone;
    }
}