package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneActivationTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testSceneActivationConstructor() {
    SceneActivation activation = new SceneActivation();
    assertNotNull(activation);
    assertNotNull(activation.getRecall());
  }

  @Test
  void testSceneActivationDefaultRecall() {
    SceneActivation activation = new SceneActivation();
    Recall recall = activation.getRecall();
    assertNotNull(recall);
    assertEquals(SceneAction.ACTIVE, recall.getAction());
  }

  @Test
  void testJsonSerialization() throws Exception {
    SceneActivation activation = new SceneActivation();
    String json = mapper.writeValueAsString(activation);
    assertNotNull(json);
    assertTrue(json.contains("recall"));
  }
}
