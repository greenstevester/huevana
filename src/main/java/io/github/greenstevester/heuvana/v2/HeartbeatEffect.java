package io.github.greenstevester.heuvana.v2;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates a heartbeat pulsing effect that mimics a heartbeat pattern:
 * quick pulse, short pause, quick pulse, longer pause, repeat.
 *
 * <p>The heartbeat pattern consists of:
 * <ul>
 *   <li>First beat: quick dim-bright-dim</li>
 *   <li>Short pause</li>
 *   <li>Second beat: quick dim-bright-dim</li>
 *   <li>Longer pause before next cycle</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * // Create a heartbeat effect with default settings
 * HeartbeatEffect.builder()
 *     .light(light)
 *     .beatCount(10)
 *     .build()
 *     .start();
 * }</pre>
 *
 * @since 5.3.0
 */
public class HeartbeatEffect {

    private final Light light;
    private final int minBrightness;
    private final int maxBrightness;
    private final Duration beatDuration;
    private final Duration pauseBetweenBeats;
    private final Duration pauseBetweenCycles;
    private final int beatCount;
    private final boolean preserveState;
    private final Runnable onComplete;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger completedBeats = new AtomicInteger(0);

    private HeartbeatEffect(final Light light, final int minBrightness, final int maxBrightness,
                           final Duration beatDuration, final Duration pauseBetweenBeats,
                           final Duration pauseBetweenCycles, final int beatCount,
                           final boolean preserveState, final Runnable onComplete) {
        this.light = light;
        this.minBrightness = minBrightness;
        this.maxBrightness = maxBrightness;
        this.beatDuration = beatDuration;
        this.pauseBetweenBeats = pauseBetweenBeats;
        this.pauseBetweenCycles = pauseBetweenCycles;
        this.beatCount = beatCount;
        this.preserveState = preserveState;
        this.onComplete = onComplete;
    }

    /**
     * Starts the heartbeat effect.
     */
    public void start() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Effect is already running");
        }

        final UpdateState initialState = preserveState ? captureCurrentState() : null;

        scheduleNextBeat(initialState);
    }

    private void scheduleNextBeat(final UpdateState initialState) {
        if (completedBeats.get() >= beatCount || !running.get()) {
            handleCompletion(initialState);
            return;
        }

        // Each heartbeat cycle has two beats
        scheduleSingleBeat(0); // First beat
        scheduleSingleBeat(beatDuration.toMillis() + pauseBetweenBeats.toMillis()); // Second beat

        // Schedule next cycle
        final long nextCycleDelay = (beatDuration.toMillis() * 2) +
                                    pauseBetweenBeats.toMillis() +
                                    pauseBetweenCycles.toMillis();

        executor.schedule(() -> {
            completedBeats.incrementAndGet();
            scheduleNextBeat(initialState);
        }, nextCycleDelay, TimeUnit.MILLISECONDS);
    }

    private void scheduleSingleBeat(final long initialDelay) {
        // Beat down to bright
        executor.schedule(() ->
            light.setBrightness(maxBrightness),
            initialDelay,
            TimeUnit.MILLISECONDS
        );

        // Beat back to dim
        executor.schedule(() ->
            light.setBrightness(minBrightness),
            initialDelay + beatDuration.toMillis(),
            TimeUnit.MILLISECONDS
        );
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
        return new UpdateState();
    }

    /**
     * Creates a new builder for HeartbeatEffect.
     *
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for HeartbeatEffect.
     */
    public static class Builder {
        private Light light;
        private int minBrightness = 10;
        private int maxBrightness = 100;
        private Duration beatDuration = Duration.ofMillis(200); // Quick beat
        private Duration pauseBetweenBeats = Duration.ofMillis(150); // Short pause
        private Duration pauseBetweenCycles = Duration.ofMillis(600); // Longer pause
        private int beatCount = Integer.MAX_VALUE; // Infinite by default
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
         * Sets the minimum brightness during the heartbeat.
         *
         * @param minBrightness Minimum brightness (1-100)
         * @return This builder
         */
        public Builder minBrightness(final int minBrightness) {
            this.minBrightness = minBrightness;
            return this;
        }

        /**
         * Sets the maximum brightness during the heartbeat.
         *
         * @param maxBrightness Maximum brightness (1-100)
         * @return This builder
         */
        public Builder maxBrightness(final int maxBrightness) {
            this.maxBrightness = maxBrightness;
            return this;
        }

        /**
         * Sets the duration of a single beat (dim to bright and back).
         *
         * @param beatDuration Beat duration
         * @return This builder
         */
        public Builder beatDuration(final Duration beatDuration) {
            this.beatDuration = beatDuration;
            return this;
        }

        /**
         * Sets the pause duration between the two beats in a heartbeat cycle.
         *
         * @param pauseBetweenBeats Pause duration
         * @return This builder
         */
        public Builder pauseBetweenBeats(final Duration pauseBetweenBeats) {
            this.pauseBetweenBeats = pauseBetweenBeats;
            return this;
        }

        /**
         * Sets the pause duration between heartbeat cycles.
         *
         * @param pauseBetweenCycles Pause duration
         * @return This builder
         */
        public Builder pauseBetweenCycles(final Duration pauseBetweenCycles) {
            this.pauseBetweenCycles = pauseBetweenCycles;
            return this;
        }

        /**
         * Sets the number of heartbeat cycles to perform. Use Integer.MAX_VALUE for infinite.
         *
         * @param beatCount Number of heartbeat cycles
         * @return This builder
         */
        public Builder beatCount(final int beatCount) {
            this.beatCount = beatCount;
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
         * Builds the HeartbeatEffect.
         *
         * @return A new HeartbeatEffect instance
         */
        public HeartbeatEffect build() {
            if (light == null) {
                throw new IllegalStateException("Light must be specified");
            }
            if (minBrightness < 1 || minBrightness > 100) {
                throw new IllegalStateException("Min brightness must be between 1 and 100");
            }
            if (maxBrightness < 1 || maxBrightness > 100) {
                throw new IllegalStateException("Max brightness must be between 1 and 100");
            }
            if (minBrightness >= maxBrightness) {
                throw new IllegalStateException("Min brightness must be less than max brightness");
            }

            return new HeartbeatEffect(light, minBrightness, maxBrightness, beatDuration,
                                      pauseBetweenBeats, pauseBetweenCycles, beatCount,
                                      preserveState, onComplete);
        }
    }
}
