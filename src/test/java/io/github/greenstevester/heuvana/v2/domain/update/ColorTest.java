package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.greenstevester.heuvana.v2.domain.Xy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testColorConstructor() {
    Color color = new Color();
    assertNotNull(color);
    assertNull(color.getXy());
  }

  @Test
  void testSetXy() {
    Color color = new Color();
    Xy xy = new Xy().setX(0.5f).setY(0.5f);
    Color result = color.setXy(xy);

    assertEquals(xy, color.getXy());
    assertEquals(0.5f, color.getXy().getX());
    assertEquals(0.5f, color.getXy().getY());
    assertSame(color, result); // Should return self for fluent chaining
  }

  @Test
  void testDifferentXyValues() {
    Color color = new Color();

    color.setXy(new Xy().setX(0.1f).setY(0.9f));
    assertEquals(0.1f, color.getXy().getX());
    assertEquals(0.9f, color.getXy().getY());

    color.setXy(new Xy().setX(0.7f).setY(0.3f));
    assertEquals(0.7f, color.getXy().getX());
    assertEquals(0.3f, color.getXy().getY());
  }

  @Test
  void testToString() {
    Color color = new Color().setXy(new Xy().setX(0.6f).setY(0.4f));
    String json = color.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    Color color = new Color().setXy(new Xy().setX(0.5f).setY(0.5f));
    String json = mapper.writeValueAsString(color);
    assertNotNull(json);
    assertTrue(json.contains("xy"));
  }
}
