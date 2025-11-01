package io.github.greenstevester.heuvana.v2;

import io.github.greenstevester.heuvana.Color;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for creating flashing/pulsing light effects.
 * This class allows continuous flashing between colors, useful for alerts and notifications.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Flash between black and white 10 times
 * FlashingEffect.builder()
 *     .light(myLight)
 *     .color1(Color.BLACK)
 *     .color2(Color.WHITE)
 *     .flashDuration(Duration.ofMillis(500))
 *     .flashCount(10)
 *     .build()
 *     .start();
 *
 * // Continuous flashing until stopped
 * FlashingEffect effect = FlashingEffect.builder()
 *     .light(myLight)
 *     .color1(Color.BLACK)
 *     .color2(Color.RED)
 *     .flashDuration(Duration.ofMillis(300))
 *     .continuous(true)
 *     .build();
 * effect.start();
 * // ... later ...
 * effect.stop();
 * }</pre>
 */
public class FlashingEffect {

    private final Light light;
    private final Color color1;
    private final Color color2;
    private final Duration flashDuration;
    private final int flashCount;
    private final boolean continuous;
    private final Runnable onComplete;

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> scheduledFuture;
    private int currentFlashCount = 0;
    private boolean isColor1 = true;

    private FlashingEffect(Builder builder) {
        this.light = builder.light;
        this.color1 = builder.color1;
        this.color2 = builder.color2;
        this.flashDuration = builder.flashDuration;
        this.flashCount = builder.flashCount;
        this.continuous = builder.continuous;
        this.onComplete = builder.onComplete;
    }

    /**
     * Start the flashing effect.
     */
    public void start() {
        if (executor != null && !executor.isShutdown()) {
            stop(); // Stop any existing effect
        }

        executor = Executors.newSingleThreadScheduledExecutor();
        currentFlashCount = 0;
        isColor1 = true;

        scheduledFuture = executor.scheduleAtFixedRate(
            this::toggleColor,
            0,
            flashDuration.toMillis(),
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Stop the flashing effect and restore the light to color2.
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

        // Restore to final color
        if (light != null) {
            light.setState(new UpdateState().color(color2).on());
        }

        if (onComplete != null) {
            onComplete.run();
        }
    }

    private void toggleColor() {
        try {
            Color colorToUse = isColor1 ? color1 : color2;
            light.setState(new UpdateState().color(colorToUse).on());
            isColor1 = !isColor1;
            currentFlashCount++;

            // Check if we should stop
            if (!continuous && currentFlashCount >= flashCount * 2) {
                stop();
            }
        } catch (Exception e) {
            // Log error but don't stop the effect
            System.err.println("Error toggling color: " + e.getMessage());
        }
    }

    /**
     * Check if the effect is currently running.
     */
    public boolean isRunning() {
        return scheduledFuture != null && !scheduledFuture.isDone();
    }

    /**
     * Creates a builder for constructing FlashingEffect instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Light light;
        private Color color1 = Color.of(0, 0, 0); // Black
        private Color color2 = Color.of(255, 255, 255); // White
        private Duration flashDuration = Duration.ofMillis(500);
        private int flashCount = 5;
        private boolean continuous = false;
        private Runnable onComplete;

        /**
         * Set the light to apply the effect to.
         */
        public Builder light(Light light) {
            this.light = light;
            return this;
        }

        /**
         * Set the first color (default: black).
         */
        public Builder color1(Color color1) {
            this.color1 = color1;
            return this;
        }

        /**
         * Set the second color (default: white).
         */
        public Builder color2(Color color2) {
            this.color2 = color2;
            return this;
        }

        /**
         * Set how long each color is shown (default: 500ms).
         */
        public Builder flashDuration(Duration flashDuration) {
            this.flashDuration = flashDuration;
            return this;
        }

        /**
         * Set how many times to flash (default: 5).
         * Ignored if continuous is true.
         */
        public Builder flashCount(int flashCount) {
            this.flashCount = flashCount;
            return this;
        }

        /**
         * Set whether to flash continuously until stopped (default: false).
         */
        public Builder continuous(boolean continuous) {
            this.continuous = continuous;
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
         * Build the FlashingEffect instance.
         */
        public FlashingEffect build() {
            if (light == null) {
                throw new IllegalStateException("Light must be set");
            }
            return new FlashingEffect(this);
        }
    }
}
