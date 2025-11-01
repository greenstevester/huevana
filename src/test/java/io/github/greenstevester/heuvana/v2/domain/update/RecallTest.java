package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecallTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testRecallConstructor() {
    Recall recall = new Recall();
    assertNotNull(recall);
    assertEquals(SceneAction.ACTIVE, recall.getAction());
  }

  @Test
  void testRecallDefaultAction() {
    Recall recall = new Recall();
    assertEquals(SceneAction.ACTIVE, recall.getAction());
  }

  @Test
  void testJsonSerialization() throws Exception {
    Recall recall = new Recall();
    String json = mapper.writeValueAsString(recall);
    assertNotNull(json);
    assertTrue(json.contains("\"action\":\"active\""));
  }
}
