package io.github.greenstevester.heuvana;

import io.github.greenstevester.heuvana.Color;
import io.github.greenstevester.heuvana.v2.ColorFadeEffect;
import io.github.greenstevester.heuvana.v2.HeartbeatEffect;
import io.github.greenstevester.heuvana.v2.Hue;
import io.github.greenstevester.heuvana.v2.Light;
import io.github.greenstevester.heuvana.v2.PulsingEffect;
import io.github.greenstevester.heuvana.v2.SunriseEffect;
import io.github.greenstevester.heuvana.v2.UpdateState;
import io.github.greenstevester.heuvana.v2.domain.update.EffectType;
import org.junit.jupiter.api.Disabled;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Interactive Quick Start Demo for huevana.
 *
 * <p>This comprehensive demo showcases all basic and advanced light control features:
 * <ol>
 *   <li>Connecting to your bridge with provided credentials</li>
 *   <li>Listing all available lights</li>
 *   <li>Selecting a light interactively</li>
 *   <li>Identifying the light (breathe effect)</li>
 *   <li>Turning light on/off</li>
 *   <li>Setting brightness levels</li>
 *   <li>Changing colors</li>
 *   <li>Running a pulsing effect</li>
 *   <li>Demonstrating native Hue v2 effects (CANDLE)</li>
 *   <li>Color fade effect (red to blue transition)</li>
 *   <li>Heartbeat effect (realistic heartbeat pattern)</li>
 *   <li>Sunrise simulation (natural dawn progression)</li>
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

            waitForUser("Press Enter to start the demo...");

            // Step 4: Identify the light
            System.out.println("🔦 Step 1: Identify Light");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("Making the light breathe so you can identify it...");
            System.out.println();
            selectedLight.setState(new UpdateState().alert());
            Thread.sleep(3000);
            System.out.println("✓ Identification complete!");
            System.out.println();

            waitForUser("Press Enter to continue...");

            // Step 5: Turn light on/off
            System.out.println("💡 Step 2: Turn Light On/Off");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("Turning light OFF...");
            selectedLight.turnOff();
            Thread.sleep(2000);
            System.out.println("✓ Light is OFF");
            System.out.println();
            System.out.println("Turning light ON...");
            selectedLight.turnOn();
            Thread.sleep(2000);
            System.out.println("✓ Light is ON");
            System.out.println();

            waitForUser("Press Enter to continue...");

            // Step 6: Set brightness
            System.out.println("🔆 Step 3: Set Brightness");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("Setting brightness to 10%...");
            selectedLight.setBrightness(10);
            Thread.sleep(2000);
            System.out.println("✓ Brightness at 10%");
            System.out.println();
            System.out.println("Setting brightness to 100%...");
            selectedLight.setBrightness(100);
            Thread.sleep(2000);
            System.out.println("✓ Brightness at 100%");
            System.out.println();
            System.out.println("Setting brightness to 50%...");
            selectedLight.setBrightness(50);
            Thread.sleep(2000);
            System.out.println("✓ Brightness at 50%");
            System.out.println();

            waitForUser("Press Enter to continue...");

            // Step 7: Change colors
            System.out.println("🎨 Step 4: Change Colors");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("Setting color to RED...");
            selectedLight.setState(new UpdateState().color(Color.of(255, 0, 0)).on());
            Thread.sleep(2000);
            System.out.println("✓ Color is RED");
            System.out.println();
            System.out.println("Setting color to GREEN...");
            selectedLight.setState(new UpdateState().color(Color.of(0, 255, 0)).on());
            Thread.sleep(2000);
            System.out.println("✓ Color is GREEN");
            System.out.println();
            System.out.println("Setting color to BLUE...");
            selectedLight.setState(new UpdateState().color(Color.of(0, 0, 255)).on());
            Thread.sleep(2000);
            System.out.println("✓ Color is BLUE");
            System.out.println();

            waitForUser("Press Enter to continue...");

            // Step 8: Run pulsing effect
            System.out.println("✨ Step 5: Pulsing Effect");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("   Min Brightness: 10%");
            System.out.println("   Max Brightness: 90%");
            System.out.println("   Pulse Duration: 1 second");
            System.out.println("   Pulse Count:    5 pulses");
            System.out.println();
            System.out.println("Watch your light pulse! 💫");
            System.out.println();

            final CountDownLatch latch = new CountDownLatch(1);

            final PulsingEffect effect = PulsingEffect.builder()
                .light(selectedLight)
                .minBrightness(10)
                .maxBrightness(90)
                .pulseDuration(Duration.ofMillis(1000))
                .pulseCount(5)
                .preserveState(true)
                .onComplete(() -> {
                    System.out.println("✓ Pulsing effect complete!");
                    latch.countDown();
                })
                .build();

            effect.start();
            latch.await(); // Wait for completion

            System.out.println();

            waitForUser("Press Enter to continue...");

            // Step 9: Demo native v2 effect
            System.out.println("🕯️  Step 6: Native Hue v2 Effect");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("   Effect: CANDLE");
            System.out.println("   Duration: 8 seconds");
            System.out.println("   Description: Flickering candle effect");
            System.out.println();
            System.out.println("Watch your light flicker like a candle! 🕯️");
            System.out.println();

            // Apply candle effect
            selectedLight.setState(new UpdateState()
                .color(Color.of(255, 147, 41))  // Warm orange
                .brightness(60)
                .effect(EffectType.CANDLE)
                .on());

            // Let it run for 8 seconds
            Thread.sleep(8000);

            // Stop the effect and restore light
            selectedLight.setState(new UpdateState().effect(EffectType.NO_EFFECT));
            System.out.println("✓ Effect stopped!");

            System.out.println();

            waitForUser("Press Enter to continue...");

            // Step 7: Color Fade Effect
            System.out.println("🌈 Step 7: Color Fade Effect");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("   From: RED");
            System.out.println("   To: BLUE");
            System.out.println("   Duration: 10 seconds");
            System.out.println();
            System.out.println("Watch the color smoothly fade! 🌈");
            System.out.println();

            final CountDownLatch fadeLatch = new CountDownLatch(1);
            ColorFadeEffect.builder()
                .light(selectedLight)
                .fromColor(Color.of(255, 0, 0))
                .toColor(Color.of(0, 0, 255))
                .duration(Duration.ofSeconds(10))
                .onComplete(() -> {
                    System.out.println("✓ Color fade complete!");
                    fadeLatch.countDown();
                })
                .build()
                .start();

            fadeLatch.await();
            System.out.println();

            waitForUser("Press Enter to continue...");

            // Step 8: Heartbeat Effect
            System.out.println("💓 Step 8: Heartbeat Effect");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("   Pattern: Two beats + pause");
            System.out.println("   Beat Count: 5");
            System.out.println();
            System.out.println("Watch the heartbeat pattern! 💓");
            System.out.println();

            final CountDownLatch heartbeatLatch = new CountDownLatch(1);
            HeartbeatEffect.builder()
                .light(selectedLight)
                .minBrightness(10)
                .maxBrightness(100)
                .beatCount(5)
                .onComplete(() -> {
                    System.out.println("✓ Heartbeat complete!");
                    heartbeatLatch.countDown();
                })
                .build()
                .start();

            heartbeatLatch.await();
            System.out.println();

            waitForUser("Press Enter to continue...");

            // Step 9: Sunrise Simulation
            System.out.println("🌅 Step 9: Sunrise Simulation");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("   Duration: 30 seconds");
            System.out.println("   Colors: Deep red → Warm orange → Bright orange → Warm yellow");
            System.out.println();
            System.out.println("Watch the natural sunrise! 🌅");
            System.out.println();

            final CountDownLatch sunriseLatch = new CountDownLatch(1);
            SunriseEffect.builder()
                .light(selectedLight)
                .duration(Duration.ofSeconds(30))
                .startBrightness(1)
                .endBrightness(100)
                .onComplete(() -> {
                    System.out.println("✓ Sunrise complete!");
                    sunriseLatch.countDown();
                })
                .build()
                .start();

            sunriseLatch.await();
            System.out.println();

            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║                                                ║");
            System.out.println("║              Demo Complete! ✨                 ║");
            System.out.println("║                                                ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Features demonstrated:");
            System.out.println("  ✓ Listing lights");
            System.out.println("  ✓ Identifying lights (breathe/alert)");
            System.out.println("  ✓ Turning lights on/off");
            System.out.println("  ✓ Setting brightness (0-100%)");
            System.out.println("  ✓ Changing colors");
            System.out.println("  ✓ Pulsing effects");
            System.out.println("  ✓ Native v2 effects (CANDLE)");
            System.out.println("  ✓ Color fade effect (red to blue)");
            System.out.println("  ✓ Heartbeat effect");
            System.out.println("  ✓ Sunrise simulation");
            System.out.println();
            System.out.println("Available native v2 effects:");
            System.out.println("  • FIRE, CANDLE, SPARKLE, PRISM");
            System.out.println("  • OPAL, GLISTEN, UNDERWATER, COSMOS");
            System.out.println("  • SUNBEAM, ENCHANT");
            System.out.println();
            System.out.println("Next steps:");
            System.out.println("  • Check out README.md for more examples");
            System.out.println("  • Explore all effects in EFFECTS_GUIDE.md");
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

    private static void waitForUser(final String message) {
        final Scanner scanner = new Scanner(System.in);
        System.out.println(message);
        scanner.nextLine();
        System.out.println();
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
