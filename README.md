huevana - a modern Java Library for Philips Hue
=====================================================
[![Maven Central](https://img.shields.io/maven-central/v/io.github.greenstevester/huevana.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.greenstevester%22%20AND%20a:%22huevana%22)
[![javadoc](https://javadoc.io/badge2/io.github.greenstevester/huevana/javadoc.svg)](https://javadoc.io/doc/io.github.greenstevester/huevana)

A modern Java library for controlling Philips Hue lights. This library accesses
the REST API of the Philips Hue Bridge directly without using the official Hue SDK.

## Prerequisites
- **Java 17 or higher** (updated from Java 8 for modern language features)
- Philips Hue Bridge (API v2 recommended)
- This library works with Android projects using API level 24 or higher
- Last confirmed compatible with Philips Hue Bridge API in August 2025

----

**NOTE: Philips has announced that plain HTTP connections with the bridges will be disabled and
replaced with HTTPS only. HTTPS connections are the default for this library.
**

----

## Installation

Add the following dependency to your `pom.xml` file if you are using Maven:

```xml
<dependency>
    <groupId>io.github.greenstevester</groupId>
    <artifactId>huevana</artifactId>
    <version>5.0.5</version>
</dependency>
```

For Gradle, add this to your `build.gradle` file:

```gradle
repositories {
  mavenCentral()
}

dependencies {
  implementation 'io.github.greenstevester:huevana:5.0.5'
}
```

## Quick Start

The fastest way to test huevana with your Hue Bridge is to run the interactive Quick Start Demo:

### Step 1: Discover Your Bridge and Get API Key

If you don't have your bridge IP and API key yet:

```bash
mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.v2.BridgeSetupManualTest" \
  -Dexec.classpathScope=test
```

This will:
1. Automatically discover your Hue Bridge
2. Prompt you to press the button on your bridge
3. Generate an API key
4. Display your bridge IP and API key

### Step 2: Run the Interactive Demo

Once you have your bridge IP and API key, run the demo:

```bash
mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.QuickStartDemo" \
  -Dexec.classpathScope=test \
  -Dexec.args="<your-bridge-ip> <your-api-key>"
```

**Example:**
```bash
mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.QuickStartDemo" \
  -Dexec.classpathScope=test \
  -Dexec.args="192.168.1.100 abc123xyz456"
```

**Or create a convenience script** (recommended for repeated use):
```bash
# Copy the template
cp run-demo.sh.template run-demo.sh

# Edit with your credentials (run-demo.sh is in .gitignore)
# Set BRIDGE_IP and API_KEY

# Make executable and run
chmod +x run-demo.sh
./run-demo.sh
```

This comprehensive demo showcases all basic and advanced light control features:
1. Connect to your bridge
2. List all your lights
3. Let you choose a light interactively
4. **Identify the light** - Make it breathe to confirm selection
5. **Turn light on/off** - Demonstrate basic power control with smooth transitions
6. **Set brightness** - Show 10%, 100%, and 50% brightness levels with smooth transitions
7. **Change colors** - Cycle through RED, GREEN, and BLUE with smooth transitions
8. **Pulsing effect** - Smooth brightness animation
9. **Native v2 effects** - Demonstrate all 10 native Philips Hue effects (FIRE, CANDLE, SPARKLE, PRISM, OPAL, GLISTEN, UNDERWATER, COSMOS, SUNBEAM, ENCHANT)
10. **Heartbeat effect** - Realistic two-beat heartbeat pattern

**What you'll see:**
```
╔════════════════════════════════════════════════╗
║           Huevana Quick Start Demo            ║
╚════════════════════════════════════════════════╝

📡 Connecting to Hue Bridge at 192.168.1.100...
✓ Connected successfully!

💡 Available Lights:
─────────────────────────────────────────────────
 1. Kitchen Island               [ON]
 2. Living Room Ceiling          [ON]
 3. Bedroom Lamp                 [OFF]

Choose a light (1-3): 1
✓ Selected: Kitchen Island

Press Enter to start the demo...

🔦 Step 1: Identify Light
─────────────────────────────────────────────────
Making the light breathe so you can identify it...
✓ Identification complete!

💡 Step 2: Turn Light On/Off
─────────────────────────────────────────────────
Turning light OFF...
✓ Light is OFF
Turning light ON...
✓ Light is ON

🔆 Step 3: Set Brightness
─────────────────────────────────────────────────
Setting brightness to 10%...
✓ Brightness at 10%
Setting brightness to 100%...
✓ Brightness at 100%
Setting brightness to 50%...
✓ Brightness at 50%

🎨 Step 4: Change Colors
─────────────────────────────────────────────────
Setting color to RED...
✓ Color is RED
Setting color to GREEN...
✓ Color is GREEN
Setting color to BLUE...
✓ Color is BLUE

✨ Step 5: Pulsing Effect
─────────────────────────────────────────────────
   Min Brightness: 10%
   Max Brightness: 90%
   Pulse Duration: 1 second
   Pulse Count:    5 pulses

Watch your light pulse! 💫
✓ Pulsing effect complete!

🎭 Step 6: Native Hue v2 Effects
─────────────────────────────────────────────────
   Demonstrating all native Philips Hue effects
   Each effect runs for 5 seconds

Watch your light transform! ✨

🔥 FIRE - Cozy fireplace effect
🕯️  CANDLE - Flickering candle effect
✨ SPARKLE - Sparkling light effect
🌈 PRISM - Prism color effects
💎 OPAL - Opal color shifts
💫 GLISTEN - Glistening shimmer effect
🌊 UNDERWATER - Underwater bubble effect
🌌 COSMOS - Cosmic space effect
☀️  SUNBEAM - Warm sunbeam effect
🔮 ENCHANT - Magical enchanted effect

✓ All effects demonstrated!

💓 Step 7: Heartbeat Effect
─────────────────────────────────────────────────
   Pattern: Two beats + pause
   Beat Count: 5

Watch the heartbeat pattern! 💓
✓ Heartbeat complete!

╔════════════════════════════════════════════════╗
║              Demo Complete! ✨                 ║
╚════════════════════════════════════════════════╝

Features demonstrated:
  ✓ Listing lights
  ✓ Identifying lights (breathe/alert)
  ✓ Turning lights on/off with smooth transitions
  ✓ Setting brightness (0-100%) with smooth transitions
  ✓ Changing colors with smooth transitions
  ✓ Pulsing effects
  ✓ Native v2 effects (all 10: FIRE, CANDLE, SPARKLE, PRISM, OPAL, GLISTEN, UNDERWATER, COSMOS, SUNBEAM, ENCHANT)
  ✓ Heartbeat effect
```

### Using huevana in Your Code

Here are code examples for the features demonstrated in the QuickStartDemo:

```java
import io.github.greenstevester.heuvana.Color;
import io.github.greenstevester.heuvana.v2.Hue;
import io.github.greenstevester.heuvana.v2.Light;
import io.github.greenstevester.heuvana.v2.UpdateState;
import io.github.greenstevester.heuvana.v2.PulsingEffect;
import io.github.greenstevester.heuvana.v2.domain.update.EffectType;
import java.time.Duration;

// Connect to bridge
Hue hue = new Hue("192.168.1.100", "your-api-key");

// Get a light
Light light = hue.getLightByName("Kitchen Island").orElseThrow();

// Identify light (breathe effect)
light.setState(new UpdateState().alert());

// Turn light on/off with smooth transitions
light.setState(new UpdateState().off().transitionTime(Duration.ofSeconds(2)));
light.setState(new UpdateState().on().transitionTime(Duration.ofSeconds(2)));

// Set brightness with smooth transitions
light.setState(new UpdateState().brightness(10).transitionTime(Duration.ofSeconds(2)));   // 10%
light.setState(new UpdateState().brightness(100).transitionTime(Duration.ofSeconds(2)));  // 100%
light.setState(new UpdateState().brightness(50).transitionTime(Duration.ofSeconds(2)));   // 50%

// Change colors with smooth transitions
light.setState(new UpdateState().color(Color.of(255, 0, 0)).on().transitionTime(Duration.ofSeconds(1)));   // Red
light.setState(new UpdateState().color(Color.of(0, 255, 0)).on().transitionTime(Duration.ofSeconds(1)));   // Green
light.setState(new UpdateState().color(Color.of(0, 0, 255)).on().transitionTime(Duration.ofSeconds(1)));   // Blue

// Pulsing effect
PulsingEffect.builder()
    .light(light)
    .minBrightness(10)
    .maxBrightness(90)
    .pulseDuration(Duration.ofMillis(1000))
    .pulseCount(5)
    .preserveState(true)
    .build()
    .start();

// Native v2 effects - CANDLE example
light.setState(new UpdateState()
    .color(Color.of(255, 147, 41))  // Warm orange
    .brightness(60)
    .effect(EffectType.CANDLE)
    .on());

// Other native v2 effects with recommended colors
light.setState(new UpdateState()
    .color(Color.of(255, 80, 0))  // Orange-red for FIRE
    .brightness(70)
    .effect(EffectType.FIRE)
    .on());

light.setState(new UpdateState()
    .color(Color.of(50, 0, 100))  // Deep purple for COSMOS
    .brightness(70)
    .effect(EffectType.COSMOS)
    .on());

// Stop any effect
light.setState(new UpdateState().effect(EffectType.NO_EFFECT));
```

## Usage

First, import the classes from this library (and some others too):

[//]: # (imports)
```java
import io.github.greenstevester.heuvana.Color;
import io.github.greenstevester.heuvana.HueBridge;
import io.github.greenstevester.heuvana.HueBridgeConnectionBuilder;
import io.github.greenstevester.heuvana.v2.Hue;
import io.github.greenstevester.heuvana.v2.Light;
import io.github.greenstevester.heuvana.v2.Group;
import io.github.greenstevester.heuvana.discovery.HueBridgeDiscoveryService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
```

### Initializing the API with a connection to the Bridge

#### Bridge discovery

If you do not know the IP address of the Bridge, you can use the automatic Bridge discovery functionality.
The `discoverBridges` method of the `HueBridgeDiscoveryService` class accepts a `Consumer`
that is called whenever a new Bridge is found. You may either hook into that or you can supply a no-op consumer
and just use the `Future<List<HueBridge>>` that is returned. Please do note, however, that it may take
approximately five seconds for the discovery process to complete. The `HueBridge` objects hold an IP address
that may be then used to initiate a connection with the Bridge.

Without any parameters besides the consumer the `discoverBridges` method uses all available discovery
methods simultaneously, namely N-UPnP and mDNS. If you wish to change that, the method accepts a varargs
list of discovery method enum values.

[//]: # (throws-InterruptedException|java.util.concurrent.ExecutionException)
```java
Future<List<HueBridge>> bridgesFuture = new HueBridgeDiscoveryService()
        .discoverBridges(bridge -> System.out.println("Bridge found: " + bridge));
final List<HueBridge> bridges = bridgesFuture.get();
if( !bridges.isEmpty() ) {
  final String bridgeIp = bridges.get(0).getIp();
  System.out.println("Bridge found at " + bridgeIp);
  // Then follow the code snippets below under the "Once you have a Bridge IP address" header
}
```

#### Once you have a Bridge IP address

If you already have an API key for your Bridge:

[//]: # (init)
```java
final String bridgeIp = "192.168.1.99"; // Fill in the IP address of your Bridge
final String apiKey = "bn4z908...34jf03jokaf4"; // Fill in an API key to access your Bridge
final Hue hue = new Hue(bridgeIp, apiKey);
```

If you don't have an API key for your bridge:

[//]: # (throws-InterruptedException|java.util.concurrent.ExecutionException)
[//]: # (import java.util.concurrent.CompletableFuture;)
```java
final String bridgeIp = "192.168.1.99"; // Fill in the IP address of your Bridge
final String appName = "MyFirstHueApp"; // Fill in the name of your application
final CompletableFuture<String> apiKey = new HueBridgeConnectionBuilder(bridgeIp).initializeApiConnection(appName);
// Push the button on your Hue Bridge to resolve the apiKey future:
final String key = apiKey.get();
System.out.println("Store this API key for future use: " + key);
final Hue hue = new Hue(bridgeIp, key);
```

### Using the rooms, lights, and scenes

#### A note on setting colors

When setting the color of a light or a room, one must use the `io.github.greenstevester.heuvana.Color` class.
There exists several ways to initialize the class using its
factory methods. `Color.of(int)` accepts a color code as an integer of the typical `0xRRGGBB` format.
You may get an integer like this from, for example, from the `java.awt.Color#getRGB()` method.
In Android environments you would use the `android.graphics.Color#toArgb()` method.
Note that in this case the alpha channel will be ignored, because a transparency value does not really
make sense in the context of lights. Alternatively, you may enter the color code as a six digit hexadecimal string
with the `Color.of(String)` method, as integer parts from 0 to 255 with the `Color.of(int, int, int)` method,
or as float parts from 0 to 1 with the `Color.of(float, float, float)` method.
Finally, you can just supply any sensible third party color object into the general `Color.of(Object)` factory method,
which will then attempt to parse it by finding its red, green and blue component methods using reflection.

In the pre-2.x.x versions of this library, one could set the color directly using `java.awt.Color` objects only.
This was all nice and fine, except for the fact that Android environments do not have that class at their disposal.

### Setting gradients

The following piece of code would set a nice red to green to blue gradient for a light strip:

[//]: # (requires-init)
[//]: # (import java.util.ArrayList;)
```java
final List<Color> colors = new ArrayList<>();
colors.add(Color.of(255, 0, 0));
colors.add(Color.of(0, 255, 0));
colors.add(Color.of(0, 0, 255));
hue.getRoomByName("room name").get()
    .getLightByName("lightstrip name").get()
    .setState(new UpdateState()
        .gradient(colors)
        .brightness(50)
        .on());
```

### Creating dynamic lighting effects

The library supports creating smooth, dynamic lighting effects using the `PulsingEffect` class. This creates a breathing/pulsing effect by gradually changing the brightness between minimum and maximum values.

#### Pulsing Effect

Create a smooth pulsing effect with configurable brightness range:

[//]: # (requires-init)
[//]: # (import io.github.greenstevester.heuvana.v2.PulsingEffect;)
[//]: # (import java.time.Duration;)
```java
// Get a light from your room
Light light = hue.getRoomByName("Kitchen")
    .flatMap(room -> room.getLightByName("Kitchen-Island"))
    .orElseThrow();

// Create a pulsing effect that breathes from 10% to 90% brightness
PulsingEffect.builder()
    .light(light)
    .minBrightness(10)              // Minimum brightness (1-100)
    .maxBrightness(90)              // Maximum brightness (1-100)
    .pulseDuration(Duration.ofMillis(2000))  // 2 seconds per pulse cycle
    .pulseCount(5)                  // Number of complete pulses
    .preserveState(true)            // Restore original state when done
    .build()
    .start();
```

#### Testing the Pulsing Effect

Manual test classes are provided to help you set up and test the pulsing effect with your own Bridge.

**First-time setup:**

1. **Run the bridge setup test** to discover your bridge and get an API key:
   ```bash
   mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.v2.BridgeSetupManualTest" \
                 -Dexec.classpathScope=test
   ```

   This will:
   - Automatically discover your Hue Bridge on the network
   - Prompt you to press the button on your bridge
   - Display your bridge IP and API key
   - Verify the connection works

2. **Copy the configuration values** displayed by the setup test

3. **Update PulsingEffectManualTest.java** with your values:
   ```java
   final String bridgeIp = "192.168.1.100";  // Replace with your bridge IP
   final String apiKey = "your-api-key-here";  // Replace with your API key
   ```

4. **Run the pulsing effect test**:
   ```bash
   mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.v2.PulsingEffectManualTest" \
                 -Dexec.classpathScope=test
   ```

The test will pulse your "Kitchen-Island" light from 10% to 90% brightness for 5 cycles. You can pass different parameters:
```bash
mvn exec:java -Dexec.mainClass="io.github.greenstevester.heuvana.v2.PulsingEffectManualTest" \
              -Dexec.classpathScope=test \
              -Dexec.args="\"Living Room Lamp\" 5 100"
```

### Advanced Custom Effects

The library provides advanced effect classes for complex custom lighting sequences:

**Color Fade Effect** - Smooth color transitions over time:
```java
// Fade from red to blue over 10 seconds
ColorFadeEffect.builder()
    .light(light)
    .fromColor(Color.of(255, 0, 0))
    .toColor(Color.of(0, 0, 255))
    .duration(Duration.ofSeconds(10))
    .build()
    .start();
```

**Heartbeat Effect** - Realistic heartbeat pulsing pattern:
```java
// Pulse like a heartbeat
HeartbeatEffect.builder()
    .light(light)
    .minBrightness(10)
    .maxBrightness(100)
    .beatCount(10)
    .build()
    .start();
```

**Sunrise Simulation** - Natural sunrise with color progression:
```java
// 20-minute wake-up sunrise
SunriseEffect.builder()
    .light(light)
    .duration(Duration.ofMinutes(20))
    .startBrightness(1)
    .endBrightness(100)
    .build()
    .start();
```

See [EFFECTS_GUIDE.md](EFFECTS_GUIDE.md) for detailed examples and use cases.

### Native Philips Hue v2 Effects

The library supports all native Philips Hue Bridge v2 API effects. These effects run continuously on the light until stopped:

**Available Effects:**
- `FIRE` - Cozy fireplace effect
- `CANDLE` - Flickering candle effect
- `SPARKLE` - Sparkling light effect
- `PRISM` - Prism color effects
- `OPAL` - Opal color shifts
- `GLISTEN` - Glistening shimmer effect
- `UNDERWATER` - Underwater bubble effect
- `COSMOS` - Cosmic space effect
- `SUNBEAM` - Warm sunbeam effect
- `ENCHANT` - Magical enchanted effect

**Example usage:**

[//]: # (requires-init)
[//]: # (import io.github.greenstevester.heuvana.v2.domain.update.EffectType;)
```java
// Apply a candle effect to create ambiance
Light light = hue.getLightByName("Table Lamp").orElseThrow();
light.setState(new UpdateState()
    .color(Color.of(255, 147, 41))  // Warm orange
    .brightness(50)
    .effect(EffectType.CANDLE)
    .on());

// Apply a cosmos effect for a space-themed atmosphere
light.setState(new UpdateState()
    .effect(EffectType.COSMOS)
    .on());

// Stop the effect
light.setState(new UpdateState().effect(EffectType.NO_EFFECT));

// Check which effects your light supports
Collection<EffectType> supportedEffects = light.getSupportedEffects();
System.out.println("Supported effects: " + supportedEffects);
```

**Note:** Not all lights support all effects. Use `light.getSupportedEffects()` to check which effects are available for your specific light model. Effects only work on individual lights, not on rooms or zones.

For more effect examples and use cases, see [EFFECTS_GUIDE.md](EFFECTS_GUIDE.md).

### Lights that belong to a room or a zone

Note that in the context of this library both rooms and zones are collectively called _groups_:

[//]: # (requires-init)
[//]: # (import java.util.Optional;)
```java
// Get a room or a zone -- returns Optional.empty() if the room does not exist, but
// let's assume we know for a fact it exists and can do the .get() right away:
final Group room = hue.getRoomByName("Basement").get();
final Group zone = hue.getZoneByName("Route to the basement").get();

// Turn the lights on, make them pink:
room.setState(new UpdateState().color(Color.of(java.awt.Color.PINK)).on());

// Make the entire room dimly lit:
room.setBrightness(10);

// Turn off that single lamp in the corner:
room.getLightByName("Corner").get().turnOff();

// Turn one of the lights green. This also demonstrates the proper use of Optionals:
final Optional<Light> light = room.getLightByName("Ceiling 1");
light.ifPresent(l -> l.setState(new UpdateState().color(Color.of(java.awt.Color.GREEN.getRGB())).on()));

// Activate a scene:
room.getSceneByName("Tropical twilight").ifPresent(Scene::activate);
```

#### Lights that do not belong to a room or a zone

All the lights are available with the `getLights()` method of the `Hue` object, regardless of whether or not
they have been added to a room or zone. For example, in order to turn on all the lights, one would do it like this:

[//]: # (requires-init)
[//]: # (import java.util.Collection;)
```java
final Map<UUID, Light> lights = hue.getLights();
lights.values().forEach(Light::turnOn);
```

### Caching

By default this library always queries the Bridge every time you query the state of a light, a room, or a sensor.
When querying the states of several items in quick succession, it would be better to use caching. Please monitor this
space for instructions once it has been reimplemented for version 3.x.x.

### Switches

Switches include, for example, Philips Hue dimmer switchers, Philips Hue Tap switches, and various Friends of Hue switches.

[//]: # (requires-init)
```java
hue.getSwitches().values().forEach(s -> System.out.println(String.format("Switch: %s; last pressed button: #%d (%s)",
    s.getName(),
    s.getLatestPressedButton().map(Button::getLatestEvent),
    s.getLatestPressedButton().map(Button::getNumber)
    )));
```

Depending on your setup, the above snippet will print something along the following lines:

```
Switch: Living room dimmer; last pressed button: #1 (SHORT_RELEASED) at 2021-01-05T12:30:33Z[UTC]
Switch: Kitcher dimmer; last pressed button: #2 (LONG_RELEASED) at 2021-01-05T06:13:18Z[UTC]
Switch: Hue tap switch 1; last pressed button: #4 (INITIAL_PRESS) at 2021-01-05T20:58:10Z[UTC]
```

### Events

The library supports listening for events from the Bridge!
See the [HueEventsTestRun.java](src/test/java/io/github/zeroone3010/yahueapi/v2/HueEventsTestRun.java)
class for an example.

### Sensors

You can also use this library to read the states of various sensors in the Hue system. The main `Hue` class
contains methods for getting temperature sensors, presence sensors (i.e. motion sensors and geofence sensors),
daylight sensors, and ambient light sensors.

### Searching for new lights and adding them into rooms

There is a method in the old `Hue` class that starts searching for new lights and returns a `Future` that will be
resolved with the found lights (if any) once the scan is finished. The scan seems to last around 45-60 seconds:

[//]: # (throws-InterruptedException|java.util.concurrent.ExecutionException)
```java
Hue hue = new Hue("bridge IP","API key");
Future<Collection<Light>> lightSearch = hue.searchForNewLights();
Collection<Light> foundLights = lightSearch.get();
System.out.println("Lights found: " + foundLights);

// If new lights have been found, you can add them into a room:

hue.getRoomByName("Living Room").ifPresent(room -> foundLights.forEach(room::addLight));
```

If you do not wish to add the new lights into a room, they will still be accessible with the `hue.getLights()` method
(along with all those lights that _are_ assigned into rooms).




Scope and philosophy
--------------------

This library is not intended to have all the possible functionality of the SDK
or the REST API. Instead it is focusing on the essentials: querying and setting
the states of the rooms and the lights. And this library should do those
essential functions well: in an intuitive and usable way for the programmer.
The number of external dependencies should be kept to a minimum.
Version numbering follows the [Semantic Versioning](https://semver.org/).

Contributing
------------

See [CONTRIBUTING.md](CONTRIBUTING.md).

Version history
---------------

See [CHANGELOG.md](CHANGELOG.md).


## Credits

This library was originally created by [ZeroOne3010](https://github.com/ZeroOne3010) and has been updated to Java 17 with modern language features and improved performance.
