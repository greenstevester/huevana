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
    assertNotNull(EffectType.OPAL);
    assertNotNull(EffectType.GLISTEN);
    assertNotNull(EffectType.UNDERWATER);
    assertNotNull(EffectType.COSMOS);
    assertNotNull(EffectType.SUNBEAM);
    assertNotNull(EffectType.ENCHANT);
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
    assertEquals("\"opal\"", mapper.writeValueAsString(EffectType.OPAL));
    assertEquals("\"glisten\"", mapper.writeValueAsString(EffectType.GLISTEN));
    assertEquals("\"underwater\"", mapper.writeValueAsString(EffectType.UNDERWATER));
    assertEquals("\"cosmos\"", mapper.writeValueAsString(EffectType.COSMOS));
    assertEquals("\"sunbeam\"", mapper.writeValueAsString(EffectType.SUNBEAM));
    assertEquals("\"enchant\"", mapper.writeValueAsString(EffectType.ENCHANT));
    assertEquals("\"no_effect\"", mapper.writeValueAsString(EffectType.NO_EFFECT));
  }

  @Test
  void testEffectTypeValues() {
    EffectType[] values = EffectType.values();
    assertEquals(12, values.length, "Should have exactly 12 effect types");
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.FIRE));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.CANDLE));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.SPARKLE));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.PRISM));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.OPAL));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.GLISTEN));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.UNDERWATER));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.COSMOS));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.SUNBEAM));
    assertTrue(java.util.Arrays.asList(values).contains(EffectType.ENCHANT));
  }

  @Test
  void testEffectTypeValueOf() {
    assertEquals(EffectType.FIRE, EffectType.valueOf("FIRE"));
    assertEquals(EffectType.CANDLE, EffectType.valueOf("CANDLE"));
    assertEquals(EffectType.SPARKLE, EffectType.valueOf("SPARKLE"));
    assertEquals(EffectType.PRISM, EffectType.valueOf("PRISM"));
    assertEquals(EffectType.OPAL, EffectType.valueOf("OPAL"));
    assertEquals(EffectType.GLISTEN, EffectType.valueOf("GLISTEN"));
    assertEquals(EffectType.UNDERWATER, EffectType.valueOf("UNDERWATER"));
    assertEquals(EffectType.COSMOS, EffectType.valueOf("COSMOS"));
    assertEquals(EffectType.SUNBEAM, EffectType.valueOf("SUNBEAM"));
    assertEquals(EffectType.ENCHANT, EffectType.valueOf("ENCHANT"));
    assertEquals(EffectType.NO_EFFECT, EffectType.valueOf("NO_EFFECT"));
    assertEquals(EffectType.UNKNOWN, EffectType.valueOf("UNKNOWN"));
  }

  @Test
  void testNewEffectsJsonDeserialization() throws Exception {
    // Test that the new effects can be deserialized from JSON
    assertEquals(EffectType.OPAL, mapper.readValue("\"opal\"", EffectType.class));
    assertEquals(EffectType.GLISTEN, mapper.readValue("\"glisten\"", EffectType.class));
    assertEquals(EffectType.UNDERWATER, mapper.readValue("\"underwater\"", EffectType.class));
    assertEquals(EffectType.COSMOS, mapper.readValue("\"cosmos\"", EffectType.class));
    assertEquals(EffectType.SUNBEAM, mapper.readValue("\"sunbeam\"", EffectType.class));
    assertEquals(EffectType.ENCHANT, mapper.readValue("\"enchant\"", EffectType.class));
  }
}
