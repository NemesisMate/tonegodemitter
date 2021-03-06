package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Swing out.
 *
 * @author toneg0d, JavaSaBr
 */
public class SwingOut extends Swing {

    public SwingOut(final float scale, @NotNull final String name) {
        super(scale, name);
    }

    @Override
    public float apply(float a) {
        a--;
        return a * a * ((scale + 1) * a + scale) + 1;
    }
}
