package io.github.greenstevester.heuvana;

import io.github.greenstevester.heuvana.v2.Hue;
import io.github.greenstevester.heuvana.v2.Light;
import io.github.greenstevester.heuvana.v2.PulsingEffect;
import org.junit.jupiter.api.Disabled;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Interactive Quick Start Demo for huevana.
 *
 * <p>This demo helps you quickly test the library with your Hue Bridge by:
 * <ol>
 *   <li>Connecting to your bridge with provided credentials</li>
 *   <li>Listing all available lights</li>
 *   <li>Letting you choose a light</li>
 *   <li>Running a pulsing effect on the selected light</li>
 * </ol>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.QuickStartDemo" \
 *   -Dexec.classpathScope=test \
 *   -Dexec.args="192.168.1.100 your-api-key-here"
 * </pre>
 *
 * <p>If you don't have a bridge IP or API key yet, run BridgeSetupManualTest first.</p>
 *
 * @see io.github.greenstevester.heuvana.v2.BridgeSetupManualTest
 */
@Disabled("Manual demo - requires user interaction")
public class QuickStartDemo {

    public static void main(final String... args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        final String bridgeIp = args[0];
        final String apiKey = args[1];

        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║                                                ║");
        System.out.println("║           Huevana Quick Start Demo            ║");
        System.out.println("║                                                ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Step 1: Connect to bridge
            System.out.println("📡 Connecting to Hue Bridge at " + bridgeIp + "...");
            final Hue hue = new Hue(bridgeIp, apiKey);
            System.out.println("✓ Connected successfully!");
            System.out.println();

            // Step 2: List all lights
            System.out.println("💡 Available Lights:");
            System.out.println("─────────────────────────────────────────────────");

            final List<Light> lights = new java.util.ArrayList<>(hue.getLights().values());

            if (lights.isEmpty()) {
                System.out.println("❌ No lights found on your bridge.");
                System.out.println();
                System.out.println("Make sure you have at least one light connected.");
                System.exit(1);
            }

            for (int i = 0; i < lights.size(); i++) {
                final Light light = lights.get(i);
                final String status = light.isOn() ? "ON" : "OFF";
                System.out.printf("%2d. %-30s [%s]%n", i + 1, light.getName(), status);
            }
            System.out.println();

            // Step 3: Let user choose a light
            final Light selectedLight = selectLight(lights);
            System.out.println();
            System.out.println("✓ Selected: " + selectedLight.getName());
            System.out.println();

            // Step 4: Run pulsing effect
            System.out.println("✨ Running pulsing effect...");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("   Min Brightness: 10%");
            System.out.println("   Max Brightness: 90%");
            System.out.println("   Pulse Duration: 1 second");
            System.out.println("   Pulse Count:    10 pulses");
            System.out.println();
            System.out.println("Watch your light pulse! 💫");
            System.out.println();

            final CountDownLatch latch = new CountDownLatch(1);

            final PulsingEffect effect = PulsingEffect.builder()
                .light(selectedLight)
                .minBrightness(10)
                .maxBrightness(90)
                .pulseDuration(Duration.ofMillis(1000))
                .pulseCount(10)
                .preserveState(true)
                .onComplete(() -> {
                    System.out.println("✓ Effect complete!");
                    latch.countDown();
                })
                .build();

            effect.start();
            latch.await(); // Wait for completion

            System.out.println();
            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║                                                ║");
            System.out.println("║              Demo Complete! ✨                 ║");
            System.out.println("║                                                ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Next steps:");
            System.out.println("  • Check out README.md for more examples");
            System.out.println("  • Explore other effects in EFFECTS_GUIDE.md");
            System.out.println("  • Read the API documentation");
            System.out.println();

        } catch (final Exception e) {
            System.err.println();
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println();
            System.err.println("Troubleshooting:");
            System.err.println("  • Verify bridge IP is correct");
            System.err.println("  • Verify API key is valid");
            System.err.println("  • Ensure bridge is on the network");
            System.err.println("  • Check firewall settings");
            System.err.println();
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Light selectLight(final List<Light> lights) {
        final Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Choose a light (1-" + lights.size() + "): ");
            final String input = scanner.nextLine().trim();

            try {
                final int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= lights.size()) {
                    return lights.get(choice - 1);
                } else {
                    System.out.println("❌ Invalid choice. Please enter a number between 1 and " + lights.size());
                }
            } catch (final NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"io.github.greenstevester.heuvana.QuickStartDemo\" \\");
        System.out.println("    -Dexec.classpathScope=test \\");
        System.out.println("    -Dexec.args=\"<bridge-ip> <api-key>\"");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"io.github.greenstevester.heuvana.QuickStartDemo\" \\");
        System.out.println("    -Dexec.classpathScope=test \\");
        System.out.println("    -Dexec.args=\"192.168.1.100 abc123xyz456\"");
        System.out.println();
        System.out.println("Need to discover your bridge and get an API key?");
        System.out.println("  Run: mvn exec:java -Dexec.mainClass=\"io.github.greenstevester.heuvana.v2.BridgeSetupManualTest\" \\");
        System.out.println("       -Dexec.classpathScope=test");
        System.out.println();
    }
}
