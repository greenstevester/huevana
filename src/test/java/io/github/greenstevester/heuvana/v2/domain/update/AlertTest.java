package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testAlertConstructor() {
    Alert alert = new Alert();
    assertNotNull(alert);
    assertNull(alert.getAction());
  }

  @Test
  void testSetAction() {
    Alert alert = new Alert();
    Alert result = alert.setAction(AlertType.BREATHE);

    assertEquals(AlertType.BREATHE, alert.getAction());
    assertSame(alert, result); // Should return self for fluent chaining
  }

  @Test
  void testAllAlertTypes() {
    Alert alert = new Alert();

    alert.setAction(AlertType.BREATHE);
    assertEquals(AlertType.BREATHE, alert.getAction());
  }

  @Test
  void testToString() {
    Alert alert = new Alert().setAction(AlertType.BREATHE);
    String json = alert.toString();
    assertNotNull(json);
    assertTrue(json.contains("action"));
  }

  @Test
  void testJsonSerialization() throws Exception {
    Alert alert = new Alert().setAction(AlertType.BREATHE);
    String json = mapper.writeValueAsString(alert);
    assertNotNull(json);
    assertTrue(json.contains("action"));
  }
}
