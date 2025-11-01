package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DimmingTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testDimmingConstructor() {
    Dimming dimming = new Dimming();
    assertNotNull(dimming);
    assertEquals(0, dimming.getBrightness());
  }

  @Test
  void testSetBrightness() {
    Dimming dimming = new Dimming();
    Dimming result = dimming.setBrightness(50);

    assertEquals(50, dimming.getBrightness());
    assertSame(dimming, result); // Should return self for fluent chaining
  }

  @Test
  void testBrightnessRange() {
    Dimming dimming = new Dimming();

    dimming.setBrightness(1);
    assertEquals(1, dimming.getBrightness());

    dimming.setBrightness(100);
    assertEquals(100, dimming.getBrightness());

    dimming.setBrightness(50);
    assertEquals(50, dimming.getBrightness());
  }

  @Test
  void testToString() {
    Dimming dimming = new Dimming().setBrightness(75);
    String json = dimming.toString();
    assertNotNull(json);
    assertTrue(json.contains("brightness"));
  }

  @Test
  void testJsonSerialization() throws Exception {
    Dimming dimming = new Dimming().setBrightness(80);
    String json = mapper.writeValueAsString(dimming);
    assertNotNull(json);
    assertTrue(json.contains("\"brightness\":80"));
  }
}
