package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class EffectTypeTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testAllEffectTypesExist() {
    // Test all effect types mentioned by user: flash, pulse, sparkle, candle, fire, prism
    // Note: flash and pulse are implemented in FlashingEffect class, not as enums
    assertNotNull(EffectType.FIRE);
    assertNotNull(EffectType.CANDLE);
    assertNotNull(EffectType.SPARKLE);
    assertNotNull(EffectType.PRISM);
    assertNotNull(EffectType.NO_EFFECT);
    assertNotNull(EffectType.UNKNOWN);
  }

  @ParameterizedTest
  @EnumSource(EffectType.class)
  void testAllEffectTypesSerializable(EffectType effectType) throws Exception {
    assertNotNull(effectType);
    String json = mapper.writeValueAsString(effectType);
    assertNotNull(json);
  }

  @Test
  void testEffectTypeJsonValues() throws Exception {
    assertEquals("\"fire\"", mapper.writeValueAsString(EffectType.FIRE));
    assertEquals("\"candle\"", mapper.writeValueAsString(EffectType.CANDLE));
    assertEquals("\"sparkle\"", mapper.writeValueAsString(EffectType.SPARKLE));
    assertEquals("\"prism\"", mapper.writeValueAsString(EffectType.PRISM));
    assertEquals("\"no_effect\"", mapper.writeValueAsString(EffectType.NO_EFFECT));
  }

  @Test
  void testEffectTypeValues() {
    EffectType[] values = EffectType.values();
    assertTrue(values.length >= 6);
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.FIRE));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.CANDLE));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.SPARKLE));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.PRISM));
  }

  @Test
  void testEffectTypeValueOf() {
    assertEquals(EffectType.FIRE, EffectType.valueOf("FIRE"));
    assertEquals(EffectType.CANDLE, EffectType.valueOf("CANDLE"));
    assertEquals(EffectType.SPARKLE, EffectType.valueOf("SPARKLE"));
    assertEquals(EffectType.PRISM, EffectType.valueOf("PRISM"));
    assertEquals(EffectType.NO_EFFECT, EffectType.valueOf("NO_EFFECT"));
    assertEquals(EffectType.UNKNOWN, EffectType.valueOf("UNKNOWN"));
  }
}
