package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DimmingDeltaTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testDimmingDeltaConstructor() {
    DimmingDelta delta = new DimmingDelta();
    assertNotNull(delta);
    assertNull(delta.getAction());
    assertEquals(0, delta.getBrightnessDelta());
  }

  @Test
  void testSetAction() {
    DimmingDelta delta = new DimmingDelta();
    DimmingDelta result = delta.setAction(DeltaAction.UP);

    assertEquals(DeltaAction.UP, delta.getAction());
    assertSame(delta, result); // Should return self for fluent chaining
  }

  @Test
  void testSetBrightnessDelta() {
    DimmingDelta delta = new DimmingDelta();
    DimmingDelta result = delta.setBrightnessDelta(20);

    assertEquals(20, delta.getBrightnessDelta());
    assertSame(delta, result); // Should return self for fluent chaining
  }

  @Test
  void testFluentBuilderPattern() {
    DimmingDelta delta = new DimmingDelta()
        .setAction(DeltaAction.DOWN)
        .setBrightnessDelta(15);

    assertEquals(DeltaAction.DOWN, delta.getAction());
    assertEquals(15, delta.getBrightnessDelta());
  }

  @Test
  void testToString() {
    DimmingDelta delta = new DimmingDelta()
        .setAction(DeltaAction.UP)
        .setBrightnessDelta(10);
    String json = delta.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    DimmingDelta delta = new DimmingDelta()
        .setAction(DeltaAction.UP)
        .setBrightnessDelta(25);
    String json = mapper.writeValueAsString(delta);
    assertNotNull(json);
    assertTrue(json.contains("\"brightness_delta\":25"));
  }
}
