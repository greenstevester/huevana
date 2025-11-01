package io.github.greenstevester.heuvana.v2.domain.event;

import io.github.greenstevester.heuvana.v2.ButtonEventType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ButtonEventTest {

  @Test
  void testButtonEventConstructorAndGetters() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();
    ButtonEventType eventType = ButtonEventType.INITIAL_PRESS;

    ButtonEvent event = new ButtonEvent(eventTime, null, null, eventType, eventGroupId);

    assertEquals(eventTime, event.getEventTime());
    assertEquals(eventType, event.getEventType());
    assertEquals(eventGroupId, event.getEventGroupId());
    assertNull(event.getSwitch());
    assertNull(event.getButton());
  }

  @Test
  void testButtonEventWithAllFields() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();
    ButtonEventType eventType = ButtonEventType.HOLD;

    ButtonEvent event = new ButtonEvent(eventTime, null, null, eventType, eventGroupId);

    assertNotNull(event);
    assertEquals(eventTime, event.getEventTime());
    assertEquals(eventType, event.getEventType());
    assertEquals(eventGroupId, event.getEventGroupId());
  }

  @Test
  void testButtonEventToString() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();
    ButtonEventType eventType = ButtonEventType.LONG_RELEASED;

    ButtonEvent event = new ButtonEvent(eventTime, null, null, eventType, eventGroupId);
    String toString = event.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("SwitchEvent"));
    assertTrue(toString.contains(eventTime));
    assertTrue(toString.contains(eventGroupId.toString()));
  }

  @Test
  void testButtonEventWithDifferentEventTypes() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();

    ButtonEvent event1 = new ButtonEvent(eventTime, null, null, ButtonEventType.INITIAL_PRESS, eventGroupId);
    assertEquals(ButtonEventType.INITIAL_PRESS, event1.getEventType());

    ButtonEvent event2 = new ButtonEvent(eventTime, null, null, ButtonEventType.HOLD, eventGroupId);
    assertEquals(ButtonEventType.HOLD, event2.getEventType());

    ButtonEvent event3 = new ButtonEvent(eventTime, null, null, ButtonEventType.SHORT_RELEASED, eventGroupId);
    assertEquals(ButtonEventType.SHORT_RELEASED, event3.getEventType());

    ButtonEvent event4 = new ButtonEvent(eventTime, null, null, ButtonEventType.LONG_RELEASED, eventGroupId);
    assertEquals(ButtonEventType.LONG_RELEASED, event4.getEventType());
  }
}
