package tonegod.emitter.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The utility class.
 *
 * @author JavaSaBr
 */
public class RandomUtils {

    /**
     * Get current random.
     *
     * @return the random.
     */
    public static @NotNull Random getRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * Returns a random integer between min and max.
     *
     * @param random the random
     * @param min    the min
     * @param max    the max
     * @return A random int between <tt>min</tt> (inclusive) to         <tt>max</tt> (inclusive).
     */
    public static int nextRandomInt(@NotNull final Random random, final int min, final int max) {
        return (int) (random.nextFloat() * (max - min + 1)) + min;
    }
}
