package io.github.greenstevester.heuvana.v2;

import org.junit.jupiter.api.Disabled;

import java.time.Duration;
import java.util.Optional;

/**
 * Manual test for the PulsingEffect class with hardcoded bridge credentials.
 * This test demonstrates the equivalent of the command:
 * ./hue effects pulse "Kitchen-Island" --min 10 --max 90
 *
 * <p><b>IMPORTANT:</b> This is a manual test that requires configuration before running.
 * Do not run this test as part of the automated test suite.</p>
 *
 * <p>To run this test:</p>
 * <ol>
 *   <li>Discover your Hue Bridge using {@link io.github.greenstevester.heuvana.discovery.HueBridgeDiscoveryService}</li>
 *   <li>Initialize an API connection using {@link io.github.greenstevester.heuvana.HueBridgeConnectionBuilder}</li>
 *   <li>Update the bridgeIp and apiKey constants in this file with your values</li>
 *   <li>Run via: {@code mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.v2.PulsingEffectManualTest" -Dexec.classpathScope=test}</li>
 * </ol>
 */
@Disabled("Manual test - requires bridge IP and API key configuration")
public class PulsingEffectManualTest {

    public static void main(final String... args) {
        final String bridgeIp = "<INSERT_YOUR_BRIDGE_IP_HERE>";
        final String apiKey = "<INSERT_YOUR_API_KEY_HERE>";
        final String lightName = args.length > 0 ? args[0] : "Kitchen-Island";
        final int minBrightness = args.length > 1 ? Integer.parseInt(args[1]) : 10;
        final int maxBrightness = args.length > 2 ? Integer.parseInt(args[2]) : 90;

        System.out.println("===================================================");
        System.out.println("  Pulsing Effect Test");
        System.out.println("===================================================");
        System.out.println("Bridge IP:     " + bridgeIp);
        System.out.println("Light Name:    " + lightName);
        System.out.println("Min Brightness: " + minBrightness + "%");
        System.out.println("Max Brightness: " + maxBrightness + "%");
        System.out.println("===================================================");
        System.out.println();

        System.out.println("Connecting to Hue Bridge at " + bridgeIp + "...");
        final Hue hue = new Hue(bridgeIp, apiKey);

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
        System.out.println("✓ Found light: " + light.getName() + " (ID: " + light.getId() + ")");
        System.out.println("  Current state: " + (light.isOn() ? "ON" : "OFF"));
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
                System.out.println("✓ Pulse effect completed!");
                System.out.println("  Light restored to original state.");
            })
            .build();

        System.out.println("▶ Effect started... (watch your light!)");
        System.out.println();

        // Show progress
        effect.start();

        try {
            for (int i = 1; i <= 5; i++) {
                Thread.sleep(2000); // Wait for one pulse cycle
                System.out.println("  Completed pulse " + i + " of 5");
            }
            Thread.sleep(1000); // Extra buffer
        } catch (InterruptedException e) {
            System.err.println("Interrupted! Stopping effect...");
            effect.stop();
            Thread.currentThread().interrupt();
        }

        // Verify the effect completed
        if (!effect.isRunning()) {
            System.out.println();
            System.out.println("✓ Effect finished successfully.");
            System.out.println("  Total pulses completed: " + effect.getCompletedPulses());
        } else {
            System.err.println("Warning: Effect is still running. Stopping...");
            effect.stop();
        }

        System.out.println();
        System.out.println("===================================================");
        System.out.println("  Test Complete");
        System.out.println("===================================================");
    }
}
