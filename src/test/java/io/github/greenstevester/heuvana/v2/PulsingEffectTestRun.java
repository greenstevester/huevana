package io.github.greenstevester.heuvana.v2;

import java.time.Duration;
import java.util.Optional;

/**
 * Manual test for the PulsingEffect class.
 * This test demonstrates the equivalent of the command:
 * ./hue effects pulse "Kitchen-Island" --min 10 --max 90
 *
 * <p>Usage:</p>
 * <pre>
 * java PulsingEffectTestRun &lt;bridge-ip&gt; &lt;api-key&gt; &lt;light-name&gt; [min-brightness] [max-brightness]
 *
 * Examples:
 * java PulsingEffectTestRun 192.168.1.100 myApiKey "Kitchen-Island"
 * java PulsingEffectTestRun 192.168.1.100 myApiKey "Kitchen-Island" 10 90
 * java PulsingEffectTestRun 192.168.1.100 myApiKey "Bedroom Light" 5 50
 * </pre>
 */
public class PulsingEffectTestRun {

    public static void main(final String... args) {
        if (args.length < 3) {
            System.err.println("Usage: java PulsingEffectTestRun <bridge-ip> <api-key> <light-name> [min-brightness] [max-brightness]");
            System.err.println();
            System.err.println("Examples:");
            System.err.println("  java PulsingEffectTestRun 192.168.1.100 myApiKey \"Kitchen-Island\"");
            System.err.println("  java PulsingEffectTestRun 192.168.1.100 myApiKey \"Kitchen-Island\" 10 90");
            System.err.println("  java PulsingEffectTestRun 192.168.1.100 myApiKey \"Bedroom Light\" 5 50");
            System.exit(1);
        }

        final String ip = args[0];
        final String apiKey = args[1];
        final String lightName = args[2];
        final int minBrightness = args.length > 3 ? Integer.parseInt(args[3]) : 10;
        final int maxBrightness = args.length > 4 ? Integer.parseInt(args[4]) : 90;

        System.out.println("Connecting to Hue Bridge at " + ip + "...");
        final Hue hue = new Hue(ip, apiKey);

        System.out.println("Bridge ID: " + hue.getBridgeId());
        System.out.println();

        // Find the light by name
        System.out.println("Searching for light: \"" + lightName + "\"...");
        Optional<Light> lightOpt = hue.getLights().values().stream()
            .filter(light -> light.getName().equals(lightName))
            .findFirst();

        if (lightOpt.isEmpty()) {
            System.err.println("Light \"" + lightName + "\" not found!");
            System.err.println();
            System.err.println("Available lights:");
            hue.getLights().values().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .forEach(light -> System.err.println("  - " + light.getName()));
            System.exit(1);
        }

        Light light = lightOpt.get();
        System.out.println("Found light: " + light.getName() + " (ID: " + light.getId() + ")");
        System.out.println("Current state: " + (light.isOn() ? "ON" : "OFF"));
        System.out.println();

        // Create and start the pulsing effect
        // Equivalent to: ./hue effects pulse "Kitchen-Island" --min 10 --max 90
        System.out.println("Starting pulse effect:");
        System.out.println("  Min brightness: " + minBrightness + "%");
        System.out.println("  Max brightness: " + maxBrightness + "%");
        System.out.println("  Pulse duration: 2000ms (2 seconds per cycle)");
        System.out.println("  Pulse count: 5");
        System.out.println("  Total duration: ~10 seconds");
        System.out.println();

        PulsingEffect effect = PulsingEffect.builder()
            .light(light)
            .minBrightness(minBrightness)
            .maxBrightness(maxBrightness)
            .pulseDuration(Duration.ofMillis(2000)) // 2 seconds per pulse cycle
            .pulseCount(5)                          // 5 complete pulses
            .preserveState(true)                    // Restore original state after
            .onComplete(() -> {
                System.out.println();
                System.out.println("Pulse effect completed!");
                System.out.println("Light restored to original state.");
            })
            .build();

        System.out.println("Effect started... (press Ctrl+C to interrupt)");
        effect.start();

        // Wait for the effect to complete
        // Total duration: 5 pulses × 2 seconds = 10 seconds
        // Add buffer time for smooth completion
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            System.err.println("Interrupted! Stopping effect...");
            effect.stop();
            Thread.currentThread().interrupt();
        }

        // Verify the effect completed
        if (!effect.isRunning()) {
            System.out.println("Effect finished successfully.");
            System.out.println("Completed pulses: " + effect.getCompletedPulses());
        } else {
            System.err.println("Warning: Effect is still running. Stopping...");
            effect.stop();
        }
    }
}
