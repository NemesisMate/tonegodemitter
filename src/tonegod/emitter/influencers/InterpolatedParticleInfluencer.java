package tonegod.emitter.influencers;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The interface for implementing interpolated particle influencers.
 *
 * @author JavaSaBr
 */
public interface InterpolatedParticleInfluencer extends ParticleInfluencer {

    /**
     * Get the count of interpolation steps.
     *
     * @return the count of interpolation steps.
     */
    int getStepCount();

    /**
     * Get an interpolation for the step.
     *
     * @param index the index of the step.
     * @return the interpolation for the step.
     * @throws RuntimeException if the index is invalid.
     */
    @NotNull
    Interpolation getInterpolation(final int index) throws RuntimeException;

    /**
     * Update a interpolation for the index.
     *
     * @param interpolation the new interpolation.
     * @param index         the index.
     * @throws RuntimeException if the index is invalid.
     */
    void updateInterpolation(final @NotNull Interpolation interpolation, final int index) throws RuntimeException;

    /**
     * Get the list of all exists interpolations.
     *
     * @return the list of interpolations.
     */
    @NotNull
    Array<Interpolation> getInterpolations();
}
