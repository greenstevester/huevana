package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertTypeTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testAlertTypeValues() {
    assertNotNull(AlertType.BREATHE);
  }

  @Test
  void testAlertTypeValueOf() {
    assertEquals(AlertType.BREATHE, AlertType.valueOf("BREATHE"));
  }

  @Test
  void testAlertTypeJsonValues() throws Exception {
    assertEquals("\"breathe\"", mapper.writeValueAsString(AlertType.BREATHE));
  }
}
