package io.github.greenstevester.heuvana.v2;

import io.github.greenstevester.heuvana.v2.domain.update.Dimming;
import io.github.greenstevester.heuvana.v2.domain.update.Dynamics;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for creating smooth brightness-based pulsing light effects.
 * This class creates a breathing/pulsing effect by interpolating brightness values between
 * a minimum and maximum level, similar to the Philips Hue pulse effect.
 *
 * <p>Each pulse cycle is divided into 10 steps: 5 steps fading up from min to max brightness,
 * followed by 5 steps fading down from max to min brightness. This creates a smooth
 * breathing effect.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Pulse between 10% and 90% brightness, 5 times
 * PulsingEffect.builder()
 *     .light(myLight)
 *     .minBrightness(10)
 *     .maxBrightness(90)
 *     .pulseDuration(Duration.ofMillis(2000))
 *     .pulseCount(5)
 *     .build()
 *     .start();
 *
 * // Continuous pulsing until stopped
 * PulsingEffect effect = PulsingEffect.builder()
 *     .light(myLight)
 *     .minBrightness(5)
 *     .maxBrightness(100)
 *     .pulseDuration(Duration.ofMillis(3000))
 *     .continuous(true)
 *     .build();
 * effect.start();
 * // ... later ...
 * effect.stop();
 * }</pre>
 */
public class PulsingEffect {

    private static final int STEPS_PER_PULSE = 10; // 5 steps up, 5 steps down

    private final Light light;
    private final int minBrightness;
    private final int maxBrightness;
    private final Duration pulseDuration;
    private final int pulseCount;
    private final boolean continuous;
    private final boolean preserveState;
    private final Runnable onComplete;

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> scheduledFuture;
    private int currentStep = 0;
    private int completedPulses = 0;
    private Boolean originalOnState;

    private PulsingEffect(Builder builder) {
        this.light = builder.light;
        this.minBrightness = builder.minBrightness;
        this.maxBrightness = builder.maxBrightness;
        this.pulseDuration = builder.pulseDuration;
        this.pulseCount = builder.pulseCount;
        this.continuous = builder.continuous;
        this.preserveState = builder.preserveState;
        this.onComplete = builder.onComplete;
    }

    /**
     * Start the pulsing effect.
     */
    public void start() {
        if (executor != null && !executor.isShutdown()) {
            stop(); // Stop any existing effect
        }

        // Save original on/off state if preservation is enabled
        if (preserveState) {
            originalOnState = light.isOn();
        }

        // Ensure light is on
        if (!light.isOn()) {
            light.setState(new UpdateState().on());
        }

        executor = Executors.newSingleThreadScheduledExecutor();
        currentStep = 0;
        completedPulses = 0;

        // Calculate step duration (each pulse has 10 steps)
        long stepDurationMs = pulseDuration.toMillis() / STEPS_PER_PULSE;

        scheduledFuture = executor.scheduleAtFixedRate(
            this::executePulseStep,
            0,
            stepDurationMs,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Stop the pulsing effect and optionally restore the original light on/off state.
     */
    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Restore original on/off state if preservation was enabled
        if (preserveState && originalOnState != null) {
            UpdateState restoreState = new UpdateState();
            if (originalOnState) {
                restoreState.on();
            } else {
                restoreState.off();
            }
            light.setState(restoreState);
        }

        if (onComplete != null) {
            onComplete.run();
        }
    }

    private void executePulseStep() {
        try {
            // Calculate brightness for current step
            int brightness = calculateBrightnessForStep(currentStep);

            // Apply brightness with smooth transition
            UpdateState state = new UpdateState()
                .brightness(brightness)
                .on();

            // Add smooth transition dynamics (1/10th of pulse duration per step)
            long transitionMs = pulseDuration.toMillis() / STEPS_PER_PULSE;
            state.getUpdateLight().setDynamics(new Dynamics().setDuration((int) transitionMs));

            light.setState(state);

            currentStep++;

            // Check if we completed a full pulse cycle (10 steps)
            if (currentStep >= STEPS_PER_PULSE) {
                currentStep = 0;
                completedPulses++;

                // Check if we should stop
                if (!continuous && completedPulses >= pulseCount) {
                    stop();
                }
            }
        } catch (Exception e) {
            // Log error but don't stop the effect
            System.err.println("Error executing pulse step: " + e.getMessage());
        }
    }

    /**
     * Calculate the brightness value for a given step in the pulse cycle.
     * Steps 0-4: Fade up from min to max
     * Steps 5-9: Fade down from max to min
     *
     * @param step Current step (0-9)
     * @return Brightness value (1-100)
     */
    private int calculateBrightnessForStep(int step) {
        if (step < 5) {
            // Fade up: steps 0-4
            double progress = step / 5.0;
            return (int) (minBrightness + (maxBrightness - minBrightness) * progress);
        } else {
            // Fade down: steps 5-9
            double progress = (step - 5) / 5.0;
            return (int) (maxBrightness - (maxBrightness - minBrightness) * progress);
        }
    }

    /**
     * Check if the effect is currently running.
     */
    public boolean isRunning() {
        return scheduledFuture != null && !scheduledFuture.isDone();
    }

    /**
     * Get the number of completed pulse cycles.
     */
    public int getCompletedPulses() {
        return completedPulses;
    }

    /**
     * Creates a builder for constructing PulsingEffect instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Light light;
        private int minBrightness = 10; // Default 10%
        private int maxBrightness = 100; // Default 100%
        private Duration pulseDuration = Duration.ofMillis(2000); // Default 2 seconds per pulse
        private int pulseCount = 5; // Default 5 pulses
        private boolean continuous = false;
        private boolean preserveState = true; // Default to preserving original state
        private Runnable onComplete;

        /**
         * Set the light to apply the effect to.
         */
        public Builder light(Light light) {
            this.light = light;
            return this;
        }

        /**
         * Set the minimum brightness percentage (default: 10).
         * Valid range: 1-100
         */
        public Builder minBrightness(int minBrightness) {
            if (minBrightness < 1 || minBrightness > 100) {
                throw new IllegalArgumentException("minBrightness must be between 1 and 100");
            }
            this.minBrightness = minBrightness;
            return this;
        }

        /**
         * Set the maximum brightness percentage (default: 100).
         * Valid range: 1-100
         */
        public Builder maxBrightness(int maxBrightness) {
            if (maxBrightness < 1 || maxBrightness > 100) {
                throw new IllegalArgumentException("maxBrightness must be between 1 and 100");
            }
            this.maxBrightness = maxBrightness;
            return this;
        }

        /**
         * Set the duration of each complete pulse cycle (default: 2000ms).
         * Each pulse cycle includes both fade-up and fade-down phases.
         */
        public Builder pulseDuration(Duration pulseDuration) {
            this.pulseDuration = pulseDuration;
            return this;
        }

        /**
         * Set how many times to pulse (default: 5).
         * Ignored if continuous is true.
         */
        public Builder pulseCount(int pulseCount) {
            this.pulseCount = pulseCount;
            return this;
        }

        /**
         * Set whether to pulse continuously until stopped (default: false).
         */
        public Builder continuous(boolean continuous) {
            this.continuous = continuous;
            return this;
        }

        /**
         * Set whether to preserve and restore the original light on/off state (default: true).
         * When true, the light will return to its original on/off state after the effect completes.
         * Note: Brightness is not preserved as it's not exposed by the Light interface.
         */
        public Builder preserveState(boolean preserveState) {
            this.preserveState = preserveState;
            return this;
        }

        /**
         * Set a callback to run when the effect completes.
         * Only called if the effect is not continuous or when stopped manually.
         */
        public Builder onComplete(Runnable onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        /**
         * Build the PulsingEffect instance.
         */
        public PulsingEffect build() {
            if (light == null) {
                throw new IllegalStateException("Light must be set");
            }
            if (minBrightness >= maxBrightness) {
                throw new IllegalStateException("minBrightness must be less than maxBrightness");
            }
            return new PulsingEffect(this);
        }
    }
}
