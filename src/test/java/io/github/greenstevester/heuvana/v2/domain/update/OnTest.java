package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OnTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testOnConstructor() {
    On on = new On(true);
    assertTrue(on.isOn());

    On off = new On(false);
    assertFalse(off.isOn());
  }

  @Test
  void testOnConstants() {
    assertNotNull(On.ON);
    assertTrue(On.ON.isOn());

    assertNotNull(On.OFF);
    assertFalse(On.OFF.isOn());
  }

  @Test
  void testSetOn() {
    On on = new On(false);
    assertFalse(on.isOn());

    On result = on.setOn(true);
    assertTrue(on.isOn());
    assertSame(on, result); // Should return self for fluent chaining
  }

  @Test
  void testToString() {
    On on = new On(true);
    String json = on.toString();
    assertNotNull(json);
    assertTrue(json.contains("on"));
  }

  @Test
  void testJsonSerialization() throws Exception {
    On on = new On(true);
    String json = mapper.writeValueAsString(on);
    assertNotNull(json);
    assertTrue(json.contains("\"on\":true"));

    On off = new On(false);
    json = mapper.writeValueAsString(off);
    assertTrue(json.contains("\"on\":false"));
  }
}
