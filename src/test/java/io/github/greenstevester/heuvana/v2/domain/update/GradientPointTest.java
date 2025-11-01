package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradientPointTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testGradientPointConstructor() {
    GradientPoint point = new GradientPoint();
    assertNotNull(point);
    assertNull(point.getColor());
  }

  @Test
  void testSetColor() {
    GradientPoint point = new GradientPoint();
    Color color = new Color();
    GradientPoint result = point.setColor(color);

    assertEquals(color, point.getColor());
    assertSame(point, result); // Should return self for fluent chaining
  }

  @Test
  void testToString() {
    GradientPoint point = new GradientPoint();
    point.setColor(new Color());
    String json = point.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    GradientPoint point = new GradientPoint();
    point.setColor(new Color());
    String json = mapper.writeValueAsString(point);
    assertNotNull(json);
    assertTrue(json.contains("color"));
  }
}
