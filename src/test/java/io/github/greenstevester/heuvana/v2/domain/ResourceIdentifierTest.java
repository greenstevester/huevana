package io.github.greenstevester.heuvana.v2.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResourceIdentifierTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testResourceIdentifierConstructor() {
    ResourceIdentifier identifier = new ResourceIdentifier();
    assertNotNull(identifier);
    assertNull(identifier.getResourceId());
    assertNull(identifier.getResourceType());
  }

  @Test
  void testSetResourceId() {
    ResourceIdentifier identifier = new ResourceIdentifier();
    UUID uuid = UUID.randomUUID();

    identifier.setResourceId(uuid);

    assertEquals(uuid, identifier.getResourceId());
  }

  @Test
  void testSetResourceType() {
    ResourceIdentifier identifier = new ResourceIdentifier();

    identifier.setResourceType(ResourceType.LIGHT);

    assertEquals(ResourceType.LIGHT, identifier.getResourceType());
  }

  @Test
  void testSetAllFields() {
    ResourceIdentifier identifier = new ResourceIdentifier();
    UUID uuid = UUID.randomUUID();

    identifier.setResourceId(uuid);
    identifier.setResourceType(ResourceType.ROOM);

    assertEquals(uuid, identifier.getResourceId());
    assertEquals(ResourceType.ROOM, identifier.getResourceType());
  }

  @Test
  void testDifferentResourceTypes() {
    ResourceIdentifier light = new ResourceIdentifier();
    light.setResourceType(ResourceType.LIGHT);
    assertEquals(ResourceType.LIGHT, light.getResourceType());

    ResourceIdentifier room = new ResourceIdentifier();
    room.setResourceType(ResourceType.ROOM);
    assertEquals(ResourceType.ROOM, room.getResourceType());

    ResourceIdentifier scene = new ResourceIdentifier();
    scene.setResourceType(ResourceType.SCENE);
    assertEquals(ResourceType.SCENE, scene.getResourceType());

    ResourceIdentifier zone = new ResourceIdentifier();
    zone.setResourceType(ResourceType.ZONE);
    assertEquals(ResourceType.ZONE, zone.getResourceType());
  }

  @Test
  void testToString() {
    ResourceIdentifier identifier = new ResourceIdentifier();
    UUID uuid = UUID.randomUUID();
    identifier.setResourceId(uuid);
    identifier.setResourceType(ResourceType.LIGHT);

    String toString = identifier.toString();
    assertNotNull(toString);
  }

  @Test
  void testJsonSerialization() throws Exception {
    ResourceIdentifier identifier = new ResourceIdentifier();
    UUID uuid = UUID.randomUUID();
    identifier.setResourceId(uuid);
    identifier.setResourceType(ResourceType.LIGHT);

    String json = mapper.writeValueAsString(identifier);
    assertNotNull(json);
    assertTrue(json.contains("\"rid\""));
    assertTrue(json.contains("\"rtype\""));
  }
}
