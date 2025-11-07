# Heuvana Effects Guide

This guide explains how to use various light effects with the Heuvana library, including flashing, pulsing, and animated effects.

## Quick Reference

### Built-in Alert Effects

The simplest way to create a flashing/pulsing effect:

```java
// Flash the light a few times (uses Hue "breathe" alert)
light.setState(new UpdateState().color(Color.RED).flash());

// Same effect, different method names
light.setState(new UpdateState().color(Color.BLACK).pulse());
light.setState(new UpdateState().color(Color.BLUE).alert());
```

The `flash()`, `pulse()`, and `alert()` methods all use the Philips Hue "breathe" alert, which makes the light pulse/flash a few times then return to its previous state.

### Continuous Effects

For continuous effects that don't stop automatically. Available effects: `FIRE`, `CANDLE`, `SPARKLE`, `PRISM`, `OPAL`, `GLISTEN`, `UNDERWATER`, `COSMOS`, `SUNBEAM`, `ENCHANT`.

```java
// Sparkle effect (one of many available)
light.setState(new UpdateState()
    .color(Color.CYAN)
    .on()
    .effect(EffectType.SPARKLE));

// Cosmos effect - creates deep space atmosphere
light.setState(new UpdateState()
    .effect(EffectType.COSMOS));

// Stop any effect
light.setState(new UpdateState().effect(EffectType.NO_EFFECT));
```

### Custom Flashing Patterns

For more control over flashing patterns, use the `FlashingEffect` class:

```java
import io.github.greenstevester.heuvana.v2.FlashingEffect;
import java.time.Duration;

// Flash between black and white 10 times
FlashingEffect.builder()
    .light(light)
    .color1(Color.BLACK)
    .color2(Color.WHITE)
    .flashDuration(Duration.ofMillis(500))
    .flashCount(10)
    .build()
    .start();

// Continuous flashing until manually stopped
FlashingEffect effect = FlashingEffect.builder()
    .light(light)
    .color1(Color.BLACK)
    .color2(Color.RED)
    .flashDuration(Duration.ofMillis(300))
    .continuous(true)
    .build();

effect.start();
// ... do something else ...
effect.stop(); // Restores to color2
```

## All Available Effects

### 1. Alert/Flash/Pulse (Breathe)

**What it does:** Makes the light pulse/breathe a few times, then returns to previous state.

**When to use:** Quick notifications, alerts, or attention-grabbing moments.

**Example:**
```java
// Set color to red and flash
light.setState(new UpdateState()
    .color(Color.RED)
    .flash()
    .on());

// Flash with current color
light.setState(new UpdateState().flash());
```

### 2. Continuous Effects

These effects run continuously until stopped with `NO_EFFECT`.

#### Fire Effect
```java
light.setState(new UpdateState()
    .color(Color.ORANGE)
    .effect(EffectType.FIRE));
```

#### Candle Effect
```java
light.setState(new UpdateState()
    .color(Color.YELLOW)
    .effect(EffectType.CANDLE));
```

#### Sparkle Effect
```java
light.setState(new UpdateState()
    .color(Color.WHITE)
    .effect(EffectType.SPARKLE));
```

#### Prism Effect
```java
light.setState(new UpdateState()
    .effect(EffectType.PRISM));
```

#### Opal Effect
```java
// Opal color shifts - creates shimmering color transitions
light.setState(new UpdateState()
    .effect(EffectType.OPAL));
```

#### Glisten Effect
```java
// Glistening light effect - creates a subtle sparkling shimmer
light.setState(new UpdateState()
    .color(Color.WHITE)
    .effect(EffectType.GLISTEN));
```

#### Underwater Effect
```java
// Underwater bubble effect - creates a gentle wavy motion
light.setState(new UpdateState()
    .color(Color.CYAN)
    .effect(EffectType.UNDERWATER));
```

#### Cosmos Effect
```java
// Cosmic space effect - creates a deep space atmosphere
light.setState(new UpdateState()
    .effect(EffectType.COSMOS));
```

#### Sunbeam Effect
```java
// Warm sunbeam effect - creates a natural sunlight atmosphere
light.setState(new UpdateState()
    .color(Color.YELLOW)
    .effect(EffectType.SUNBEAM));
```

#### Enchant Effect
```java
// Magical enchanted effect - creates a mystical atmosphere
light.setState(new UpdateState()
    .effect(EffectType.ENCHANT));
```

#### Stop Effect
```java
light.setState(new UpdateState()
    .effect(EffectType.NO_EFFECT));
```

**Note:** Not all lights support all effects. Check supported effects:
```java
List<EffectType> supportedEffects = light.getSupportedEffects();
```

### 3. Timed Effects

Effects that run for a specific duration.

#### Sunrise Effect
```java
// Gradual sunrise over 30 minutes
light.setState(new UpdateState()
    .sunrise(Duration.ofMinutes(30)));

// Stop the sunrise early
light.setState(new UpdateState().clearTimedEffect());
```

### 4. Custom Flashing Effect

For complete control over flashing patterns:

```java
FlashingEffect effect = FlashingEffect.builder()
    .light(light)
    .color1(Color.BLACK)       // First color
    .color2(Color.CYAN)        // Second color
    .flashDuration(Duration.ofMillis(300)) // Time per color
    .flashCount(5)             // Number of complete cycles
    .continuous(false)         // Stop after flashCount
    .onComplete(() -> {        // Callback when done
        System.out.println("Flashing complete!");
    })
    .build();

effect.start();

// Check if running
if (effect.isRunning()) {
    // Stop manually
    effect.stop();
}
```

## Common Use Cases

### Alert for Important Event
```java
// Quick red flash
light.setState(new UpdateState().color(Color.RED).flash());
```

### Epic Surf Detected
```java
// Black pulsing to indicate epic conditions
light.setState(new UpdateState().color(Color.BLACK).pulse());

// Or continuous dramatic flashing
FlashingEffect.builder()
    .light(light)
    .color1(Color.BLACK)
    .color2(Color.CYAN)
    .flashDuration(Duration.ofMillis(400))
    .continuous(true)
    .build()
    .start();
```

### Severe Weather Warning
```java
// Rapid red/white flashing
FlashingEffect.builder()
    .light(light)
    .color1(Color.RED)
    .color2(Color.WHITE)
    .flashDuration(Duration.ofMillis(200))
    .flashCount(15)
    .build()
    .start();
```

### Romantic Dinner Mode
```java
// Gentle candle flicker
light.setState(new UpdateState()
    .color(Color.ORANGE)
    .brightness(30)
    .effect(EffectType.CANDLE));
```

### Wake Up Naturally
```java
// 20-minute sunrise
light.setState(new UpdateState()
    .sunrise(Duration.ofMinutes(20)));
```

## Combining with Colors

All effects can be combined with color and brightness settings:

```java
light.setState(new UpdateState()
    .color(Color.PURPLE)
    .brightness(80)
    .flash()
    .on());

light.setState(new UpdateState()
    .color(Color.BLUE)
    .brightness(50)
    .effect(EffectType.SPARKLE));
```

## Effect Limitations

1. **Not all lights support effects** - Check `light.getSupportedEffects()` first
2. **Rooms and zones don't support effects** - Effects work on individual lights only
3. **Only one effect at a time** - Setting a new effect cancels the previous one
4. **FlashingEffect requires manual management** - Remember to call `stop()` when done with continuous flashing

## Best Practices

1. **Always check support** before using effects on lights
2. **Use flash() for one-time alerts** - It automatically returns to normal
3. **Use FlashingEffect for custom patterns** - When you need specific timing or colors
4. **Stop continuous effects** - Always clean up with `.effect(NO_EFFECT)` or `.stop()`
5. **Consider timing** - Don't flash too rapidly (< 200ms) as it can be disorienting
6. **Test on actual hardware** - Effects may look different on different light types

## Thread Safety

The `FlashingEffect` class uses its own executor service and is safe to use from any thread. However, be sure to call `stop()` to properly clean up resources:

```java
FlashingEffect effect = FlashingEffect.builder()
    .light(light)
    .color1(Color.BLACK)
    .color2(Color.WHITE)
    .continuous(true)
    .build();

effect.start();

// Always clean up!
Runtime.getRuntime().addShutdownHook(new Thread(effect::stop));
```
