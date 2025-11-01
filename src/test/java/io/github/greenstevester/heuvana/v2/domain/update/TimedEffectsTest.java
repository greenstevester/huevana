package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TimedEffectsTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testTimedEffectsConstructor() {
    TimedEffects timedEffects = new TimedEffects();
    assertNotNull(timedEffects);
    assertNull(timedEffects.getEffect());
    assertEquals(0, timedEffects.getDuration());
  }

  @Test
  void testSetEffect() {
    TimedEffects timedEffects = new TimedEffects();
    TimedEffects result = timedEffects.setEffect(TimedEffectType.SUNRISE);

    assertEquals(TimedEffectType.SUNRISE, timedEffects.getEffect());
    assertSame(timedEffects, result); // Should return self for fluent chaining
  }

  @Test
  void testSetDuration() {
    TimedEffects timedEffects = new TimedEffects();
    TimedEffects result = timedEffects.setDuration(Duration.ofSeconds(30));

    assertEquals(30000, timedEffects.getDuration());
    assertSame(timedEffects, result); // Should return self for fluent chaining
  }

  @Test
  void testSetDurationInMilliseconds() {
    TimedEffects timedEffects = new TimedEffects();
    timedEffects.setDuration(Duration.ofMillis(45000));

    assertEquals(45000, timedEffects.getDuration());
  }

  @Test
  void testMaxDurationLimit() {
    // Maximum duration is 6 hours (21600000 milliseconds)
    TimedEffects timedEffects = new TimedEffects();
    timedEffects.setDuration(Duration.ofHours(10)); // Try to set 10 hours

    // Should be capped at 6 hours
    assertEquals(21600000L, timedEffects.getDuration());
  }

  @Test
  void testDurationWithinLimit() {
    TimedEffects timedEffects = new TimedEffects();
    timedEffects.setDuration(Duration.ofHours(5));

    // 5 hours should be allowed
    assertEquals(Duration.ofHours(5).toMillis(), timedEffects.getDuration());
  }

  @Test
  void testNullDuration() {
    TimedEffects timedEffects = new TimedEffects();
    timedEffects.setDuration(null);

    assertEquals(0, timedEffects.getDuration());
  }

  @Test
  void testFluentBuilderPattern() {
    TimedEffects timedEffects = new TimedEffects()
        .setEffect(TimedEffectType.SUNRISE)
        .setDuration(Duration.ofMinutes(30));

    assertEquals(TimedEffectType.SUNRISE, timedEffects.getEffect());
    assertEquals(Duration.ofMinutes(30).toMillis(), timedEffects.getDuration());
  }

  @Test
  void testToString() {
    TimedEffects timedEffects = new TimedEffects()
        .setEffect(TimedEffectType.SUNRISE)
        .setDuration(Duration.ofSeconds(60));
    String json = timedEffects.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    TimedEffects timedEffects = new TimedEffects()
        .setEffect(TimedEffectType.SUNRISE)
        .setDuration(Duration.ofSeconds(120));
    String json = mapper.writeValueAsString(timedEffects);
    assertNotNull(json);
    assertTrue(json.contains("\"duration\":120000"));
  }
}
