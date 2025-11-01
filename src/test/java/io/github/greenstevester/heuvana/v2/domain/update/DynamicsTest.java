package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicsTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testDynamicsConstructor() {
    Dynamics dynamics = new Dynamics();
    assertNotNull(dynamics);
    assertEquals(0, dynamics.getDuration());
    assertEquals(0.0f, dynamics.getSpeed());
  }

  @Test
  void testSetDuration() {
    Dynamics dynamics = new Dynamics();
    Dynamics result = dynamics.setDuration(1000);

    assertEquals(1000, dynamics.getDuration());
    assertSame(dynamics, result); // Should return self for fluent chaining
  }

  @Test
  void testSetSpeed() {
    Dynamics dynamics = new Dynamics();
    Dynamics result = dynamics.setSpeed(0.5f);

    assertEquals(0.5f, dynamics.getSpeed());
    assertSame(dynamics, result); // Should return self for fluent chaining
  }

  @Test
  void testFluentBuilderPattern() {
    Dynamics dynamics = new Dynamics()
        .setDuration(2000)
        .setSpeed(0.75f);

    assertEquals(2000, dynamics.getDuration());
    assertEquals(0.75f, dynamics.getSpeed());
  }

  @Test
  void testToString() {
    Dynamics dynamics = new Dynamics().setDuration(1500);
    String json = dynamics.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    Dynamics dynamics = new Dynamics()
        .setDuration(3000)
        .setSpeed(1.0f);
    String json = mapper.writeValueAsString(dynamics);
    assertNotNull(json);
    assertTrue(json.contains("\"duration\":3000"));
  }
}
