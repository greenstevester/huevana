package io.github.greenstevester.heuvana.v2;

import io.github.greenstevester.heuvana.HueBridge;
import io.github.greenstevester.heuvana.HueBridgeConnectionBuilder;
import io.github.greenstevester.heuvana.discovery.HueBridgeDiscoveryService;
import org.junit.jupiter.api.Disabled;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Manual test to discover Hue Bridge and initialize API connection.
 * This test helps developers get the bridgeIp and apiKey needed for other manual tests.
 *
 * <p><b>IMPORTANT:</b> This is a manual test that requires user interaction.
 * Do not run this test as part of the automated test suite.</p>
 *
 * <p>To run this test:</p>
 * <ol>
 *   <li>Run via: {@code mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.v2.BridgeSetupManualTest" -Dexec.classpathScope=test}</li>
 *   <li>Wait for bridge discovery to complete (~5 seconds)</li>
 *   <li>When prompted, press the button on your Hue Bridge</li>
 *   <li>Copy the displayed bridge IP and API key to use in other tests</li>
 * </ol>
 *
 * <p>The output will show you the values to use in {@link PulsingEffectManualTest}:</p>
 * <pre>
 * Bridge IP: 10.0.0.101
 * API Key: BfBT1mUW6KbE7crFvAD8b5SB9TP3zdodWgWBPhd5
 * </pre>
 */
@Disabled("Manual test - requires user interaction with physical bridge")
public class BridgeSetupManualTest {

    public static void main(final String... args) {
        System.out.println("===================================================");
        System.out.println("  Hue Bridge Setup - Discovery & API Key");
        System.out.println("===================================================");
        System.out.println();

        try {
            // Step 1: Discover bridges
            System.out.println("Step 1: Discovering Hue Bridges on your network...");
            System.out.println("(This may take up to 5 seconds)");
            System.out.println();

            Future<List<HueBridge>> bridgesFuture = new HueBridgeDiscoveryService()
                .discoverBridges(bridge ->
                    System.out.println("  ✓ Found bridge at: " + bridge.getIp())
                );

            List<HueBridge> bridges = bridgesFuture.get();

            if (bridges.isEmpty()) {
                System.err.println("❌ No Hue Bridges found on your network!");
                System.err.println();
                System.err.println("Troubleshooting:");
                System.err.println("  1. Ensure your Hue Bridge is powered on");
                System.err.println("  2. Check that your computer is on the same network as the bridge");
                System.err.println("  3. Verify the bridge has a solid blue light");
                System.err.println("  4. Try restarting your bridge and running this test again");
                System.exit(1);
            }

            System.out.println();
            System.out.println("Bridge discovery complete!");
            System.out.println("Found " + bridges.size() + " bridge(s):");
            for (int i = 0; i < bridges.size(); i++) {
                System.out.println("  [" + (i + 1) + "] " + bridges.get(i).getIp());
            }
            System.out.println();

            // Use the first bridge found
            HueBridge bridge = bridges.get(0);
            String bridgeIp = bridge.getIp();

            System.out.println("Using bridge at: " + bridgeIp);
            System.out.println();

            // Step 2: Initialize API connection
            System.out.println("Step 2: Initializing API connection...");
            System.out.println();
            System.out.println("┌─────────────────────────────────────────────────┐");
            System.out.println("│  IMPORTANT: Press the button on your Hue       │");
            System.out.println("│  Bridge NOW to authorize this application      │");
            System.out.println("│                                                 │");
            System.out.println("│  You have 30 seconds to press the button       │");
            System.out.println("└─────────────────────────────────────────────────┘");
            System.out.println();

            String appName = "HuevanaDevSetup";
            CompletableFuture<String> apiKeyFuture = new HueBridgeConnectionBuilder(bridgeIp)
                .initializeApiConnection(appName);

            System.out.println("Waiting for button press...");

            // Add a progress indicator
            for (int i = 0; i < 30; i++) {
                if (apiKeyFuture.isDone()) {
                    break;
                }
                System.out.print(".");
                Thread.sleep(1000);
            }
            System.out.println();
            System.out.println();

            String apiKey = apiKeyFuture.get();

            // Step 3: Display results
            System.out.println("✓ Connection successful!");
            System.out.println();
            System.out.println("===================================================");
            System.out.println("  Configuration Values");
            System.out.println("===================================================");
            System.out.println();
            System.out.println("Copy these values to use in your manual tests:");
            System.out.println();
            System.out.println("┌─────────────────────────────────────────────────┐");
            System.out.println("│  Bridge IP:  " + String.format("%-33s", bridgeIp) + "│");
            System.out.println("│  API Key:    " + String.format("%-33s", apiKey.substring(0, Math.min(33, apiKey.length()))) + "│");
            if (apiKey.length() > 33) {
                System.out.println("│              " + String.format("%-33s", apiKey.substring(33)) + "│");
            }
            System.out.println("└─────────────────────────────────────────────────┘");
            System.out.println();
            System.out.println("To use in PulsingEffectManualTest, update these lines:");
            System.out.println();
            System.out.println("  final String bridgeIp = \"" + bridgeIp + "\";");
            System.out.println("  final String apiKey = \"" + apiKey + "\";");
            System.out.println();
            System.out.println("⚠️  IMPORTANT: Keep your API key secure!");
            System.out.println("   Do not commit it to version control.");
            System.out.println();

            // Step 4: Verify connection works
            System.out.println("Step 3: Verifying connection...");
            Hue hue = new Hue(bridgeIp, apiKey);
            String bridgeId = hue.getBridgeId();

            System.out.println("✓ Successfully connected to bridge: " + bridgeId);
            System.out.println();
            System.out.println("Available lights:");
            hue.getLights().values().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .forEach(light -> System.out.println("  - " + light.getName()));
            System.out.println();

            System.out.println("Available rooms:");
            hue.getRooms().values().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .forEach(room -> System.out.println("  - " + room.getName()));
            System.out.println();

            System.out.println("===================================================");
            System.out.println("  Setup Complete!");
            System.out.println("===================================================");
            System.out.println();
            System.out.println("You can now use these values in:");
            System.out.println("  • PulsingEffectManualTest");
            System.out.println("  • Any other manual integration tests");
            System.out.println();

        } catch (InterruptedException e) {
            System.err.println();
            System.err.println("❌ Setup interrupted!");
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (ExecutionException e) {
            System.err.println();
            System.err.println("❌ Setup failed!");
            System.err.println();

            if (e.getCause() != null && e.getCause().getMessage() != null) {
                String message = e.getCause().getMessage();

                if (message.contains("link button not pressed")) {
                    System.err.println("Error: Button was not pressed in time.");
                    System.err.println();
                    System.err.println("Please try again and press the button on your bridge");
                    System.err.println("when prompted.");
                } else if (message.contains("Timeout")) {
                    System.err.println("Error: Connection timed out.");
                    System.err.println();
                    System.err.println("Make sure your bridge is reachable at the displayed IP.");
                } else {
                    System.err.println("Error: " + message);
                }
            } else {
                System.err.println("Error: " + e.getMessage());
            }

            System.exit(1);
        } catch (Exception e) {
            System.err.println();
            System.err.println("❌ Unexpected error during setup!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
