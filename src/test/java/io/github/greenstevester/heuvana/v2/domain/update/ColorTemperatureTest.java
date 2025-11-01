package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorTemperatureTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testColorTemperatureConstructor() {
    ColorTemperature colorTemp = new ColorTemperature();
    assertNotNull(colorTemp);
    assertNull(colorTemp.getMirek());
  }

  @Test
  void testSetMirek() {
    ColorTemperature colorTemp = new ColorTemperature();
    ColorTemperature result = colorTemp.setMirek(300);

    assertEquals(300, colorTemp.getMirek());
    assertSame(colorTemp, result); // Should return self for fluent chaining
  }

  @Test
  void testMirekRange() {
    ColorTemperature colorTemp = new ColorTemperature();

    colorTemp.setMirek(153); // Warm white
    assertEquals(153, colorTemp.getMirek());

    colorTemp.setMirek(500); // Cool white
    assertEquals(500, colorTemp.getMirek());

    colorTemp.setMirek(300); // Mid range
    assertEquals(300, colorTemp.getMirek());
  }

  @Test
  void testToString() {
    ColorTemperature colorTemp = new ColorTemperature().setMirek(250);
    String json = colorTemp.toString();
    assertNotNull(json);
  }

  @Test
  void testJsonSerialization() throws Exception {
    ColorTemperature colorTemp = new ColorTemperature().setMirek(400);
    String json = mapper.writeValueAsString(colorTemp);
    assertNotNull(json);
    assertTrue(json.contains("\"mirek\":400"));
  }
}
