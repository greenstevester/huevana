package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateLightTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testUpdateLightBuilderPattern() {
    UpdateLight update = new UpdateLight()
        .setOn(new On(true))
        .setDimming(new Dimming().setBrightness(50))
        .setAlert(new Alert().setAction(AlertType.BREATHE));

    assertNotNull(update.getOn());
    assertTrue(update.getOn().isOn());
    assertNotNull(update.getDimming());
    assertEquals(50, update.getDimming().getBrightness());
    assertNotNull(update.getAlert());
    assertEquals(AlertType.BREATHE, update.getAlert().getAction());
  }

  @Test
  void testUpdateLightWithColorTemperature() {
    ColorTemperature colorTemp = new ColorTemperature().setMirek(300);
    UpdateLight update = new UpdateLight().setColorTemperature(colorTemp);

    assertNotNull(update.getColorTemperature());
    assertEquals(300, update.getColorTemperature().getMirek());
  }

  @Test
  void testUpdateLightWithColorTemperatureDelta() {
    ColorTemperatureDelta delta = new ColorTemperatureDelta()
        .setAction(DeltaAction.UP)
        .setMirekDelta(50);
    UpdateLight update = new UpdateLight();
    update.setColorTemperatureDelta(delta);

    assertNotNull(update.getColorTemperatureDelta());
    assertEquals(DeltaAction.UP, update.getColorTemperatureDelta().getAction());
    assertEquals(50, update.getColorTemperatureDelta().getMirekDelta());
  }

  @Test
  void testUpdateLightWithColor() {
    Color color = new Color().setXy(new io.github.greenstevester.heuvana.v2.domain.Xy().setX(0.5f).setY(0.5f));
    UpdateLight update = new UpdateLight().setColor(color);

    assertNotNull(update.getColor());
    assertNotNull(update.getColor().getXy());
  }

  @Test
  void testUpdateLightWithDynamics() {
    Dynamics dynamics = new Dynamics().setDuration(1000);
    UpdateLight update = new UpdateLight().setDynamics(dynamics);

    assertNotNull(update.getDynamics());
    assertEquals(1000, update.getDynamics().getDuration());
  }

  @Test
  void testUpdateLightWithGradient() {
    Gradient gradient = new Gradient();
    UpdateLight update = new UpdateLight().setGradient(gradient);

    assertNotNull(update.getGradient());
  }

  @Test
  void testUpdateLightWithEffects() {
    Effects effects = new Effects().setEffect(EffectType.CANDLE);
    UpdateLight update = new UpdateLight().setEffects(effects);

    assertNotNull(update.getEffects());
    assertEquals(EffectType.CANDLE, update.getEffects().getEffect());
  }

  @Test
  void testUpdateLightWithTimedEffects() {
    TimedEffects timedEffects = new TimedEffects()
        .setEffect(TimedEffectType.SUNRISE)
        .setDuration(java.time.Duration.ofSeconds(30));
    UpdateLight update = new UpdateLight().setTimedEffects(timedEffects);

    assertNotNull(update.getTimedEffects());
    assertEquals(TimedEffectType.SUNRISE, update.getTimedEffects().getEffect());
    assertEquals(30000, update.getTimedEffects().getDuration());
  }

  @Test
  void testUpdateLightWithDimmingDelta() {
    DimmingDelta delta = new DimmingDelta()
        .setAction(DeltaAction.UP)
        .setBrightnessDelta(10);
    UpdateLight update = new UpdateLight().setDimmingDelta(delta);

    assertNotNull(update.getDimmingDelta());
    assertEquals(DeltaAction.UP, update.getDimmingDelta().getAction());
    assertEquals(10, update.getDimmingDelta().getBrightnessDelta());
  }

  @Test
  void testUpdateLightToString() {
    UpdateLight update = new UpdateLight()
        .setOn(new On(true))
        .setDimming(new Dimming().setBrightness(75));

    String json = update.toString();
    assertNotNull(json);
    assertTrue(json.contains("on"));
    assertTrue(json.contains("dimming"));
  }

  @Test
  void testUpdateLightJsonSerialization() throws Exception {
    UpdateLight update = new UpdateLight()
        .setOn(new On(true))
        .setDimming(new Dimming().setBrightness(80));

    String json = mapper.writeValueAsString(update);
    assertNotNull(json);
    assertTrue(json.contains("\"on\""));
    assertTrue(json.contains("\"brightness\":80"));
  }
}
