package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.greenstevester.heuvana.v2.domain.ResourceIdentifier;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateRoomTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testUpdateRoomConstructor() {
    UpdateRoom room = new UpdateRoom();
    assertNotNull(room);
    assertNull(room.getChildren());
  }

  @Test
  void testSetChildren() {
    UpdateRoom room = new UpdateRoom();
    ResourceIdentifier child1 = new ResourceIdentifier();
    child1.setResourceId(java.util.UUID.randomUUID());
    ResourceIdentifier child2 = new ResourceIdentifier();
    child2.setResourceId(java.util.UUID.randomUUID());
    List<ResourceIdentifier> children = Arrays.asList(child1, child2);

    room.setChildren(children);

    assertEquals(children, room.getChildren());
    assertEquals(2, room.getChildren().size());
  }

  @Test
  void testJsonSerialization() throws Exception {
    UpdateRoom room = new UpdateRoom();
    ResourceIdentifier child = new ResourceIdentifier();
    child.setResourceId(java.util.UUID.randomUUID());
    room.setChildren(Arrays.asList(child));

    String json = mapper.writeValueAsString(room);
    assertNotNull(json);
    assertTrue(json.contains("children"));
  }
}
