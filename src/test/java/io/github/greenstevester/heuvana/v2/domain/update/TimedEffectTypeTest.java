package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimedEffectTypeTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testTimedEffectTypeValues() {
    assertNotNull(TimedEffectType.SUNRISE);
    assertNotNull(TimedEffectType.NO_EFFECT);
    assertNotNull(TimedEffectType.UNKNOWN);
  }

  @Test
  void testTimedEffectTypeValueOf() {
    assertEquals(TimedEffectType.SUNRISE, TimedEffectType.valueOf("SUNRISE"));
    assertEquals(TimedEffectType.NO_EFFECT, TimedEffectType.valueOf("NO_EFFECT"));
    assertEquals(TimedEffectType.UNKNOWN, TimedEffectType.valueOf("UNKNOWN"));
  }

  @Test
  void testTimedEffectTypeJsonValues() throws Exception {
    assertEquals("\"sunrise\"", mapper.writeValueAsString(TimedEffectType.SUNRISE));
    assertEquals("\"no_effect\"", mapper.writeValueAsString(TimedEffectType.NO_EFFECT));
  }
}
