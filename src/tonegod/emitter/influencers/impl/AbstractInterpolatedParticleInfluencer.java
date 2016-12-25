package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.UnsafeArray;
import tonegod.emitter.influencers.InterpolatedParticleInfluencer;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;

/**
 * The base implementation of the {@link InterpolatedParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractInterpolatedParticleInfluencer extends AbstractParticleInfluencer implements InterpolatedParticleInfluencer {

    /**
     * The list of interpolations.
     */
    private final UnsafeArray<Interpolation> interpolations;

    /**
     * The fixed duration.
     */
    private float fixedDuration;

    /**
     * The blend value.
     */
    protected float blend;

    /**
     * The flag of cycling changing.
     */
    private boolean cycle;

    public AbstractInterpolatedParticleInfluencer() {
        this.interpolations = ArrayFactory.newUnsafeArray(Interpolation.class);
    }

    @Override
    public final int getStepCount() {
        return interpolations.size();
    }

    /**
     * Add a new interpolation to the list.
     */
    protected final void addInterpolation(@NotNull final Interpolation interpolation) {
        interpolations.add(interpolation);
    }

    /**
     * Remove the interpolation from the list.
     */
    protected final void removeInterpolation(final int index) {
        interpolations.slowRemove(index);
    }

    /**
     * Remove all interpolations from the list.
     */
    protected final void clearInterpolations() {
        interpolations.clear();
    }

    @Override
    public final boolean isCycle() {
        return cycle;
    }

    /**
     * @param cycle the flag of cycling changing.
     */
    protected final void setCycle(final boolean cycle) {
        this.cycle = cycle;
    }

    @Override
    public final void setFixedDuration(final float fixedDuration) {
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
    public final float getFixedDuration() {
        return fixedDuration;
    }

    @NotNull
    @Override
    public final Interpolation getInterpolation(final int index) throws RuntimeException {
        if (index < 0 || index >= interpolations.size()) {
            throw new RuntimeException("The index " + index + " isn't correct.");
        }
        return interpolations.get(index);
    }

    @Override
    public final void updateInterpolation(final @NotNull Interpolation interpolation, final int index) throws RuntimeException {
        if (index < 0 || index >= interpolations.size()) {
            throw new RuntimeException("The index " + index + " isn't correct.");
        }
        interpolations.set(index, interpolation);
    }

    @NotNull
    @Override
    public final Array<Interpolation> getInterpolations() {
        return interpolations;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final int[] interpolationIds = interpolations.stream()
                .mapToInt(InterpolationManager::getId).toArray();

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(interpolationIds, "interpolations", null);
        capsule.write(cycle, "cycle", false);
        capsule.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);
        final int[] interpolationIds = capsule.readIntArray("interpolations", null);

        ArrayUtils.forEach(interpolationIds, interpolations,
                (id, toStore) -> toStore.add(InterpolationManager.getInterpolation(id)));

        cycle = capsule.readBoolean("cycle", false);
        fixedDuration = capsule.readFloat("fixedDuration", 0.125f);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final AbstractInterpolatedParticleInfluencer clone = (AbstractInterpolatedParticleInfluencer) super.clone();
        clone.interpolations.addAll(interpolations);
        clone.cycle = cycle;
        clone.fixedDuration = fixedDuration;
        return clone;
    }
}
