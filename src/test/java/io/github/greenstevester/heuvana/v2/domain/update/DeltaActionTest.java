package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeltaActionTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testDeltaActionValues() {
    assertNotNull(DeltaAction.UP);
    assertNotNull(DeltaAction.DOWN);
    assertNotNull(DeltaAction.STOP);
  }

  @Test
  void testDeltaActionValueOf() {
    assertEquals(DeltaAction.UP, DeltaAction.valueOf("UP"));
    assertEquals(DeltaAction.DOWN, DeltaAction.valueOf("DOWN"));
    assertEquals(DeltaAction.STOP, DeltaAction.valueOf("STOP"));
  }

  @Test
  void testDeltaActionJsonValues() throws Exception {
    assertEquals("\"up\"", mapper.writeValueAsString(DeltaAction.UP));
    assertEquals("\"down\"", mapper.writeValueAsString(DeltaAction.DOWN));
    assertEquals("\"stop\"", mapper.writeValueAsString(DeltaAction.STOP));
  }
}
