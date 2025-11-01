package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GradientTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testGradientConstructor() {
    Gradient gradient = new Gradient();
    assertNotNull(gradient);
    assertNull(gradient.getPoints());
  }

  @Test
  void testSetPoints() {
    Gradient gradient = new Gradient();
    GradientPoint point1 = new GradientPoint().setColor(new Color());
    GradientPoint point2 = new GradientPoint().setColor(new Color());
    List<GradientPoint> points = Arrays.asList(point1, point2);

    Gradient result = gradient.setPoints(points);

    assertEquals(points, gradient.getPoints());
    assertEquals(2, gradient.getPoints().size());
    assertSame(gradient, result); // Should return self for fluent chaining
  }

  @Test
  void testMultiplePoints() {
    Gradient gradient = new Gradient();
    List<GradientPoint> points = Arrays.asList(
        new GradientPoint().setColor(new Color()),
        new GradientPoint().setColor(new Color()),
        new GradientPoint().setColor(new Color()),
        new GradientPoint().setColor(new Color()),
        new GradientPoint().setColor(new Color())
    );
    gradient.setPoints(points);

    assertEquals(5, gradient.getPoints().size());
  }

  @Test
  void testToString() {
    Gradient gradient = new Gradient();
    String json = gradient.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    Gradient gradient = new Gradient();
    gradient.setPoints(Arrays.asList(
        new GradientPoint().setColor(new Color()),
        new GradientPoint().setColor(new Color())
    ));
    String json = mapper.writeValueAsString(gradient);
    assertNotNull(json);
    assertTrue(json.contains("points"));
  }
}
