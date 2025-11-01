package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class EffectsTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testEffectsConstructor() {
    Effects effects = new Effects();
    assertNotNull(effects);
    assertNull(effects.getEffect());
  }

  @ParameterizedTest
  @EnumSource(value = EffectType.class, names = {"FIRE", "CANDLE", "SPARKLE", "PRISM"})
  void testSetEffectForAllTypes(EffectType effectType) {
    Effects effects = new Effects();
    Effects result = effects.setEffect(effectType);

    assertEquals(effectType, effects.getEffect());
    assertSame(effects, result); // Should return self for fluent chaining
  }

  @Test
  void testSetEffectFire() {
    Effects effects = new Effects().setEffect(EffectType.FIRE);
    assertEquals(EffectType.FIRE, effects.getEffect());
  }

  @Test
  void testSetEffectCandle() {
    Effects effects = new Effects().setEffect(EffectType.CANDLE);
    assertEquals(EffectType.CANDLE, effects.getEffect());
  }

  @Test
  void testSetEffectSparkle() {
    Effects effects = new Effects().setEffect(EffectType.SPARKLE);
    assertEquals(EffectType.SPARKLE, effects.getEffect());
  }

  @Test
  void testSetEffectPrism() {
    Effects effects = new Effects().setEffect(EffectType.PRISM);
    assertEquals(EffectType.PRISM, effects.getEffect());
  }

  @Test
  void testSetEffectNoEffect() {
    Effects effects = new Effects().setEffect(EffectType.NO_EFFECT);
    assertEquals(EffectType.NO_EFFECT, effects.getEffect());
  }

  @Test
  void testToString() {
    Effects effects = new Effects().setEffect(EffectType.FIRE);
    String json = effects.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    Effects effects = new Effects().setEffect(EffectType.CANDLE);
    String json = mapper.writeValueAsString(effects);
    assertNotNull(json);
    assertTrue(json.contains("candle"));
  }
}
