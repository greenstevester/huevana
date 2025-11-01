package io.github.greenstevester.heuvana.v2.domain.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MotionEventTest {

  @Test
  void testMotionEventConstructorAndGetters() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();
    boolean motion = true;
    boolean motionValid = true;

    MotionEvent event = new MotionEvent(eventTime, eventGroupId, null, motion, motionValid);

    assertEquals(eventTime, event.getEventTime());
    assertEquals(eventGroupId, event.getEventGroupId());
    assertTrue(event.isMotion());
    assertTrue(event.isMotionValid());
    assertNull(event.getOwner());
  }

  @Test
  void testMotionEventWithNoMotion() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();

    MotionEvent event = new MotionEvent(eventTime, eventGroupId, null, false, true);

    assertFalse(event.isMotion());
    assertTrue(event.isMotionValid());
  }

  @Test
  void testMotionEventWithInvalidMotion() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();

    MotionEvent event = new MotionEvent(eventTime, eventGroupId, null, true, false);

    assertTrue(event.isMotion());
    assertFalse(event.isMotionValid());
  }

  @Test
  void testMotionEventToString() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();

    MotionEvent event = new MotionEvent(eventTime, eventGroupId, null, true, true);
    String toString = event.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("MotionEvent"));
    assertTrue(toString.contains(eventTime));
    assertTrue(toString.contains(eventGroupId.toString()));
    assertTrue(toString.contains("motion=true"));
    assertTrue(toString.contains("motionValid=true"));
  }

  @Test
  void testMotionEventAllCombinations() {
    String eventTime = "2025-11-01T10:00:00";
    UUID eventGroupId = UUID.randomUUID();

    // Motion detected and valid
    MotionEvent event1 = new MotionEvent(eventTime, eventGroupId, null, true, true);
    assertTrue(event1.isMotion() && event1.isMotionValid());

    // Motion detected but invalid
    MotionEvent event2 = new MotionEvent(eventTime, eventGroupId, null, true, false);
    assertTrue(event2.isMotion() && !event2.isMotionValid());

    // No motion detected but valid
    MotionEvent event3 = new MotionEvent(eventTime, eventGroupId, null, false, true);
    assertTrue(!event3.isMotion() && event3.isMotionValid());

    // No motion detected and invalid
    MotionEvent event4 = new MotionEvent(eventTime, eventGroupId, null, false, false);
    assertTrue(!event4.isMotion() && !event4.isMotionValid());
  }
}
