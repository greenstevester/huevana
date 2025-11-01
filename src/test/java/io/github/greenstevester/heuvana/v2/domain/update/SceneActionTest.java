package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneActionTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testSceneActionValues() {
    assertNotNull(SceneAction.ACTIVE);
    assertNotNull(SceneAction.DYNAMIC_PALETTE);
    assertNotNull(SceneAction.STATIC);
  }

  @Test
  void testSceneActionValueOf() {
    assertEquals(SceneAction.ACTIVE, SceneAction.valueOf("ACTIVE"));
    assertEquals(SceneAction.DYNAMIC_PALETTE, SceneAction.valueOf("DYNAMIC_PALETTE"));
    assertEquals(SceneAction.STATIC, SceneAction.valueOf("STATIC"));
  }

  @Test
  void testSceneActionJsonValues() throws Exception {
    assertEquals("\"active\"", mapper.writeValueAsString(SceneAction.ACTIVE));
    assertEquals("\"dynamic_palette\"", mapper.writeValueAsString(SceneAction.DYNAMIC_PALETTE));
    assertEquals("\"static\"", mapper.writeValueAsString(SceneAction.STATIC));
  }
}
