package io.github.greenstevester.heuvana.v2.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XyTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testXyConstructor() {
    Xy xy = new Xy();
    assertNotNull(xy);
    assertEquals(0.0f, xy.getX());
    assertEquals(0.0f, xy.getY());
  }

  @Test
  void testSetX() {
    Xy xy = new Xy();
    Xy result = xy.setX(0.5f);

    assertEquals(0.5f, xy.getX());
    assertSame(xy, result); // Should return self for fluent chaining
  }

  @Test
  void testSetY() {
    Xy xy = new Xy();
    Xy result = xy.setY(0.7f);

    assertEquals(0.7f, xy.getY());
    assertSame(xy, result); // Should return self for fluent chaining
  }

  @Test
  void testFluentBuilderPattern() {
    Xy xy = new Xy()
        .setX(0.3f)
        .setY(0.6f);

    assertEquals(0.3f, xy.getX());
    assertEquals(0.6f, xy.getY());
  }

  @Test
  void testXyColorGamutBoundaries() {
    // Test typical CIE 1931 color space boundaries
    Xy xy = new Xy();

    xy.setX(0.0f).setY(0.0f);
    assertEquals(0.0f, xy.getX());
    assertEquals(0.0f, xy.getY());

    xy.setX(1.0f).setY(1.0f);
    assertEquals(1.0f, xy.getX());
    assertEquals(1.0f, xy.getY());

    xy.setX(0.5f).setY(0.5f);
    assertEquals(0.5f, xy.getX());
    assertEquals(0.5f, xy.getY());
  }

  @Test
  void testToString() {
    Xy xy = new Xy().setX(0.4f).setY(0.6f);
    String json = xy.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    Xy xy = new Xy().setX(0.3f).setY(0.7f);
    String json = mapper.writeValueAsString(xy);
    assertNotNull(json);
    assertTrue(json.contains("\"x\""));
    assertTrue(json.contains("\"y\""));
  }

  @Test
  void testJsonDeserialization() throws Exception {
    String json = "{\"x\":0.5,\"y\":0.6}";
    Xy xy = mapper.readValue(json, Xy.class);
    assertNotNull(xy);
    assertEquals(0.5f, xy.getX(), 0.001f);
    assertEquals(0.6f, xy.getY(), 0.001f);
  }
}
