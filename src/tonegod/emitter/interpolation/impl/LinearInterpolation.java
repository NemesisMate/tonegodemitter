package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class LinearInterpolation extends AbstractInterpolation {

    public LinearInterpolation(final @NotNull String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        return a;
    }
}