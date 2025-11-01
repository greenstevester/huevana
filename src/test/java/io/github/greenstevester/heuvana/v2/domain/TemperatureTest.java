package io.github.greenstevester.heuvana.v2.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testTemperatureGetters() throws Exception {
    String json = "{\"temperature\":21.5,\"temperature_valid\":true}";
    Temperature temp = mapper.readValue(json, Temperature.class);

    assertNotNull(temp);
    assertEquals(new BigDecimal("21.5"), temp.getTemperature());
    assertTrue(temp.isTemperatureValid());
  }

  @Test
  void testTemperatureWithInvalidValue() throws Exception {
    String json = "{\"temperature\":25.0,\"temperature_valid\":false}";
    Temperature temp = mapper.readValue(json, Temperature.class);

    assertNotNull(temp);
    assertEquals(new BigDecimal("25.0"), temp.getTemperature());
    assertFalse(temp.isTemperatureValid());
  }

  @Test
  void testTemperatureToString() throws Exception {
    String json = "{\"temperature\":20.0,\"temperature_valid\":true}";
    Temperature temp = mapper.readValue(json, Temperature.class);

    String toString = temp.toString();
    assertNotNull(toString);
  }

  @Test
  void testTemperatureJsonSerialization() throws Exception {
    String json = "{\"temperature\":22.5,\"temperature_valid\":true}";
    Temperature temp = mapper.readValue(json, Temperature.class);

    String serialized = mapper.writeValueAsString(temp);
    assertNotNull(serialized);
  }
}
