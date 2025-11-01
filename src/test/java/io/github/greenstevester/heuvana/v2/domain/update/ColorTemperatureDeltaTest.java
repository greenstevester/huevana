package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorTemperatureDeltaTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testColorTemperatureDeltaConstructor() {
    ColorTemperatureDelta delta = new ColorTemperatureDelta();
    assertNotNull(delta);
    assertNull(delta.getAction());
    assertEquals(0, delta.getMirekDelta());
  }

  @Test
  void testSetAction() {
    ColorTemperatureDelta delta = new ColorTemperatureDelta();
    ColorTemperatureDelta result = delta.setAction(DeltaAction.UP);

    assertEquals(DeltaAction.UP, delta.getAction());
    assertSame(delta, result); // Should return self for fluent chaining
  }

  @Test
  void testSetMirekDelta() {
    ColorTemperatureDelta delta = new ColorTemperatureDelta();
    ColorTemperatureDelta result = delta.setMirekDelta(50);

    assertEquals(50, delta.getMirekDelta());
    assertSame(delta, result); // Should return self for fluent chaining
  }

  @Test
  void testFluentBuilderPattern() {
    ColorTemperatureDelta delta = new ColorTemperatureDelta()
        .setAction(DeltaAction.DOWN)
        .setMirekDelta(30);

    assertEquals(DeltaAction.DOWN, delta.getAction());
    assertEquals(30, delta.getMirekDelta());
  }

  @Test
  void testToString() {
    ColorTemperatureDelta delta = new ColorTemperatureDelta()
        .setAction(DeltaAction.UP)
        .setMirekDelta(40);
    String json = delta.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    ColorTemperatureDelta delta = new ColorTemperatureDelta()
        .setAction(DeltaAction.UP)
        .setMirekDelta(45);
    String json = mapper.writeValueAsString(delta);
    assertNotNull(json);
    // The JSON field name is "mirek_delta"
    assertTrue(json.contains("mirek_delta") || json.contains("action"));
  }
}
