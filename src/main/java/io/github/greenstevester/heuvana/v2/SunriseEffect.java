package io.github.greenstevester.heuvana.v2;

import io.github.greenstevester.heuvana.Color;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Creates a sunrise simulation effect that gradually transitions from dark warm colors
 * to bright daylight colors, mimicking a natural sunrise.
 *
 * <p>The sunrise simulation progresses through these phases:
 * <ul>
 *   <li>Deep red/orange (dawn)</li>
 *   <li>Warm orange (early sunrise)</li>
 *   <li>Bright orange/yellow (sunrise)</li>
 *   <li>Warm white (morning light)</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * // 20-minute sunrise simulation
 * SunriseEffect.builder()
 *     .light(light)
 *     .duration(Duration.ofMinutes(20))
 *     .build()
 *     .start();
 * }</pre>
 *
 * @since 5.3.0
 */
public class SunriseEffect {

    // Sunrise color progression
    private static final Color DEEP_RED = Color.of(80, 20, 0);      // Pre-dawn
    private static final Color WARM_ORANGE = Color.of(200, 80, 0);  // Early sunrise
    private static final Color BRIGHT_ORANGE = Color.of(255, 140, 0); // Sunrise
    private static final Color WARM_YELLOW = Color.of(255, 220, 150); // Morning

    private final Light light;
    private final Duration duration;
    private final int startBrightness;
    private final int endBrightness;
    private final int steps;
    private final Runnable onComplete;

    private volatile ScheduledExecutorService executor;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private SunriseEffect(final Light light, final Duration duration, final int startBrightness,
                         final int endBrightness, final int steps, final Runnable onComplete) {
        this.light = light;
        this.duration = duration;
        this.startBrightness = startBrightness;
        this.endBrightness = endBrightness;
        this.steps = steps;
        this.onComplete = onComplete;
    }

    /**
     * Starts the sunrise effect.
     */
    public void start() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Effect is already running");
        }

        // Create executor - done here to avoid resource leak if effect is never started
        executor = Executors.newSingleThreadScheduledExecutor();

        final long delayMs = duration.toMillis() / steps;

        // Validate delay is reasonable
        if (delayMs < 10) {
            running.set(false);
            executor.shutdown();
            throw new IllegalStateException(
                "Duration too short for number of steps (minimum 10ms per step required)");
        }

        // Set initial state - very dim deep red
        light.setState(new UpdateState()
                .color(DEEP_RED)
                .brightness(startBrightness)
                .on());

        // Schedule first step, which will schedule subsequent steps
        scheduleStep(1, delayMs);
    }

    /**
     * Schedules a single step, which will recursively schedule the next step.
     *
     * @param step Current step number (1-based)
     * @param delayMs Delay between steps in milliseconds
     */
    private void scheduleStep(final int step, final long delayMs) {
        if (!running.get() || step > steps) {
            return;
        }

        executor.schedule(() -> {
            // Double-check running flag at execution time
            if (!running.get()) {
                return;
            }

            final float progress = (float) step / steps;

            // Determine color based on progress through sunrise
            final Color color = getSunriseColor(progress);
            final int brightness = calculateBrightness(progress);

            light.setState(new UpdateState()
                    .color(color)
                    .brightness(brightness)
                    .on());

            // On final step
            if (step == steps) {
                handleCompletion();
            } else {
                // Check running flag again before scheduling next step to avoid race condition
                if (running.get()) {
                    scheduleStep(step + 1, delayMs);
                }
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the effect immediately.
     */
    public void stop() {
        running.set(false);
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    /**
     * Returns whether the effect is currently running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running.get();
    }

    private void handleCompletion() {
        running.set(false);

        // Proper executor shutdown with timeout
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Execute callback with exception handling to prevent user code from breaking cleanup
        if (onComplete != null) {
            try {
                onComplete.run();
            } catch (Exception e) {
                // Log error but don't propagate to avoid breaking effect cleanup
                System.err.println("Error in SunriseEffect completion callback: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the appropriate color for the current progress through the sunrise.
     *
     * @param progress Progress from 0.0 to 1.0
     * @return The sunrise color at this progress point
     */
    private Color getSunriseColor(final float progress) {
        if (progress < 0.25f) {
            // 0-25%: Transition from deep red to warm orange
            return interpolateColor(DEEP_RED, WARM_ORANGE, progress / 0.25f);
        } else if (progress < 0.60f) {
            // 25-60%: Transition from warm orange to bright orange
            return interpolateColor(WARM_ORANGE, BRIGHT_ORANGE, (progress - 0.25f) / 0.35f);
        } else {
            // 60-100%: Transition from bright orange to warm yellow
            return interpolateColor(BRIGHT_ORANGE, WARM_YELLOW, (progress - 0.60f) / 0.40f);
        }
    }

    /**
     * Calculates the brightness for the current progress through the sunrise.
     *
     * @param progress Progress from 0.0 to 1.0
     * @return Brightness value (1-100)
     */
    private int calculateBrightness(final float progress) {
        return (int) (startBrightness + (endBrightness - startBrightness) * progress);
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
     * Creates a new builder for SunriseEffect.
     *
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for SunriseEffect.
     */
    public static class Builder {
        private Light light;
        private Duration duration = Duration.ofMinutes(20);
        private int startBrightness = 1;
        private int endBrightness = 100;
        private int steps = 100; // 100 steps for smooth transition
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
         * Sets the duration of the sunrise simulation.
         *
         * @param duration Total duration
         * @return This builder
         */
        public Builder duration(final Duration duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Sets the starting brightness (at dawn).
         *
         * @param startBrightness Starting brightness (1-100)
         * @return This builder
         */
        public Builder startBrightness(final int startBrightness) {
            this.startBrightness = startBrightness;
            return this;
        }

        /**
         * Sets the ending brightness (at full daylight).
         *
         * @param endBrightness Ending brightness (1-100)
         * @return This builder
         */
        public Builder endBrightness(final int endBrightness) {
            this.endBrightness = endBrightness;
            return this;
        }

        /**
         * Sets the number of steps for the sunrise (more steps = smoother).
         *
         * @param steps Number of transition steps
         * @return This builder
         */
        public Builder steps(final int steps) {
            this.steps = steps;
            return this;
        }

        /**
         * Sets a callback to run when the sunrise completes.
         *
         * @param onComplete Completion callback
         * @return This builder
         */
        public Builder onComplete(final Runnable onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        /**
         * Builds the SunriseEffect.
         *
         * @return A new SunriseEffect instance
         */
        public SunriseEffect build() {
            if (light == null) {
                throw new IllegalStateException("Light must be specified");
            }
            if (startBrightness < 1 || startBrightness > 100) {
                throw new IllegalStateException("Start brightness must be between 1 and 100");
            }
            if (endBrightness < 1 || endBrightness > 100) {
                throw new IllegalStateException("End brightness must be between 1 and 100");
            }
            if (startBrightness >= endBrightness) {
                throw new IllegalStateException("Start brightness must be less than end brightness");
            }
            if (steps < 10) {
                throw new IllegalStateException("Steps must be at least 10 for a smooth sunrise");
            }

            return new SunriseEffect(light, duration, startBrightness, endBrightness, steps, onComplete);
        }
    }
}
