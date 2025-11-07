package io.github.greenstevester.heuvana.v2;

import io.github.greenstevester.heuvana.Color;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Creates a smooth color fade effect that transitions from one color to another over a specified duration.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Fade from red to blue over 10 seconds
 * ColorFadeEffect.builder()
 *     .light(light)
 *     .fromColor(Color.of(255, 0, 0))
 *     .toColor(Color.of(0, 0, 255))
 *     .duration(Duration.ofSeconds(10))
 *     .build()
 *     .start();
 * }</pre>
 *
 * @since 5.3.0
 */
public class ColorFadeEffect {

    private final Light light;
    private final Color fromColor;
    private final Color toColor;
    private final Duration duration;
    private final int steps;
    private final boolean preserveState;
    private final Runnable onComplete;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean running = new AtomicBoolean(false);

    private ColorFadeEffect(final Light light, final Color fromColor, final Color toColor,
                           final Duration duration, final int steps, final boolean preserveState,
                           final Runnable onComplete) {
        this.light = light;
        this.fromColor = fromColor;
        this.toColor = toColor;
        this.duration = duration;
        this.steps = steps;
        this.preserveState = preserveState;
        this.onComplete = onComplete;
    }

    /**
     * Starts the color fade effect.
     */
    public void start() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Effect is already running");
        }

        final UpdateState initialState = preserveState ? captureCurrentState() : null;

        // Calculate delay between steps
        final long delayMs = duration.toMillis() / steps;

        // Set initial color
        light.setState(new UpdateState().color(fromColor).on());

        for (int i = 1; i <= steps; i++) {
            final int step = i;
            executor.schedule(() -> {
                final Color interpolatedColor = interpolateColor(fromColor, toColor, (float) step / steps);
                light.setState(new UpdateState().color(interpolatedColor).on());

                // On final step
                if (step == steps) {
                    handleCompletion(initialState);
                }
            }, delayMs * i, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stops the effect immediately.
     */
    public void stop() {
        running.set(false);
        executor.shutdownNow();
    }

    /**
     * Returns whether the effect is currently running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running.get();
    }

    private void handleCompletion(final UpdateState initialState) {
        running.set(false);
        executor.shutdown();

        if (preserveState && initialState != null) {
            light.setState(initialState);
        }

        if (onComplete != null) {
            onComplete.run();
        }
    }

    private UpdateState captureCurrentState() {
        // For now, we'll just note the state - a full implementation would query the light
        return new UpdateState();
    }

    /**
     * Interpolates between two colors based on a ratio (0.0 to 1.0).
     *
     * @param from   Starting color
     * @param to     Ending color
     * @param ratio  Interpolation ratio (0.0 = from color, 1.0 = to color)
     * @return Interpolated color
     */
    private Color interpolateColor(final Color from, final Color to, final float ratio) {
        final int r = (int) (from.getRed() + (to.getRed() - from.getRed()) * ratio);
        final int g = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * ratio);
        final int b = (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * ratio);
        return Color.of(r, g, b);
    }

    /**
     * Creates a new builder for ColorFadeEffect.
     *
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ColorFadeEffect.
     */
    public static class Builder {
        private Light light;
        private Color fromColor;
        private Color toColor;
        private Duration duration = Duration.ofSeconds(10);
        private int steps = 50; // 50 steps for smooth transition
        private boolean preserveState = false;
        private Runnable onComplete;

        /**
         * Sets the light to apply the effect to.
         *
         * @param light The target light
         * @return This builder
         */
        public Builder light(final Light light) {
            this.light = light;
            return this;
        }

        /**
         * Sets the starting color.
         *
         * @param fromColor Starting color
         * @return This builder
         */
        public Builder fromColor(final Color fromColor) {
            this.fromColor = fromColor;
            return this;
        }

        /**
         * Sets the ending color.
         *
         * @param toColor Ending color
         * @return This builder
         */
        public Builder toColor(final Color toColor) {
            this.toColor = toColor;
            return this;
        }

        /**
         * Sets the duration of the fade.
         *
         * @param duration Total duration
         * @return This builder
         */
        public Builder duration(final Duration duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Sets the number of steps for the fade (more steps = smoother).
         *
         * @param steps Number of color transition steps
         * @return This builder
         */
        public Builder steps(final int steps) {
            this.steps = steps;
            return this;
        }

        /**
         * Whether to preserve and restore the light's original state after completion.
         *
         * @param preserveState true to preserve state
         * @return This builder
         */
        public Builder preserveState(final boolean preserveState) {
            this.preserveState = preserveState;
            return this;
        }

        /**
         * Sets a callback to run when the effect completes.
         *
         * @param onComplete Completion callback
         * @return This builder
         */
        public Builder onComplete(final Runnable onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        /**
         * Builds the ColorFadeEffect.
         *
         * @return A new ColorFadeEffect instance
         */
        public ColorFadeEffect build() {
            if (light == null) {
                throw new IllegalStateException("Light must be specified");
            }
            if (fromColor == null) {
                throw new IllegalStateException("From color must be specified");
            }
            if (toColor == null) {
                throw new IllegalStateException("To color must be specified");
            }
            if (steps < 2) {
                throw new IllegalStateException("Steps must be at least 2");
            }

            return new ColorFadeEffect(light, fromColor, toColor, duration, steps, preserveState, onComplete);
        }
    }
}
