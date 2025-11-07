package io.github.greenstevester.heuvana.v2;

import io.github.greenstevester.heuvana.v2.domain.update.Color;
import io.github.greenstevester.heuvana.XAndYAndBrightness;
import io.github.greenstevester.heuvana.v2.domain.Xy;
import io.github.greenstevester.heuvana.v2.domain.update.Alert;
import io.github.greenstevester.heuvana.v2.domain.update.AlertType;
import io.github.greenstevester.heuvana.v2.domain.update.Dimming;
import io.github.greenstevester.heuvana.v2.domain.update.EffectType;
import io.github.greenstevester.heuvana.v2.domain.update.Effects;
import io.github.greenstevester.heuvana.v2.domain.update.Gradient;
import io.github.greenstevester.heuvana.v2.domain.update.GradientPoint;
import io.github.greenstevester.heuvana.v2.domain.update.TimedEffectType;
import io.github.greenstevester.heuvana.v2.domain.update.TimedEffects;
import io.github.greenstevester.heuvana.v2.domain.update.UpdateLight;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.greenstevester.heuvana.v2.domain.update.On.OFF;
import static io.github.greenstevester.heuvana.v2.domain.update.On.ON;

public class UpdateState {

  public static final int LOWEST_POSSIBLE_BRIGHTNESS = 0;
  public static final int MAX_BRIGHTNESS = 100;

  private final UpdateLight updateLight;

  public UpdateState() {
    this.updateLight = new UpdateLight();
  }

  /**
   * Makes this state turn the light(s) on.
   *
   * @return This state, for easy chaining of different methods.
   * @see #off()
   * @see #on(boolean)
   */
  public UpdateState on() {
    updateLight.setOn(ON);
    return this;
  }

  /**
   * Makes this state turn the light(s) off.
   *
   * @return This state, for easy chaining of different methods.
   * @see #on()
   * @see #on(boolean)
   */
  public UpdateState off() {
    updateLight.setOn(OFF);
    return this;
  }

  /**
   * Toggles the on/off status of the light(s).
   *
   * @param on {@code true} to turn on, {@code false} to turn off.
   * @return This state, for easy chaining of different methods.
   * @see #on()
   * @see #off()
   */
  public UpdateState on(final boolean on) {
    updateLight.setOn(on ? ON : OFF);
    return this;
  }

  /**
   * @param brightness Brightness, from 0 to 100, 0 setting brightness to the lowest possible value.
   * @return This state, for easy chaining of different methods.
   */
  public UpdateState brightness(final int brightness) {
    final int clippedBrightness = Math.min(Math.max(brightness, LOWEST_POSSIBLE_BRIGHTNESS), MAX_BRIGHTNESS);
    updateLight.setDimming(new Dimming().setBrightness(clippedBrightness));
    return this;
  }

  /**
   * One way of setting the color of the light(s).
   *
   * @param color A {@link io.github.greenstevester.heuvana.Color} object to specify a color.
   * @return This state, for easy chaining of different methods.
   * @see #xy(float, float)
   */
  public UpdateState color(final io.github.greenstevester.heuvana.Color color) {
    final XAndYAndBrightness xy = XAndYAndBrightness.rgbToXy(color);
    updateLight.setColor(new Color()
            .setXy(new Xy().setX(xy.getX()).setY(xy.getY())))
        .setDimming(new Dimming().setBrightness(xy.getBrightnessMax100()));
    return this;
  }

  /**
   * Other way of setting the color of the light(s): CIE XY gamut position.
   *
   * @param x A value from 0 to 1.
   * @param y A value from 0 to 1.
   * @return This state, for easy chaining of different methods.
   * @see #color(io.github.greenstevester.heuvana.Color)
   */
  public UpdateState xy(final float x, final float y) {
    updateLight.setColor(new Color().setXy(new Xy().setX(x).setY(y)));
    return this;
  }

  /**
   * Starts an effect, or stops it with the {@link EffectType#NO_EFFECT}.
   * Note that not all lights, not even all the color ones, support effects.
   * You may check the supported effects with the {@link Light#getSupportedEffects()} method.
   * Also note that rooms and zones do not support effects.
   *
   * @param effectType Type of effect.
   * @return This state, for easy chaining of different methods.
   */
  public UpdateState effect(final EffectType effectType) {
    updateLight.setEffects(new Effects().setEffect(effectType));
    return this;
  }

  /**
   * Alerts, i.e. flashes the light a few times using breathe effect.
   *
   * @return This state, for easy chaining of different methods.
   * @see #flash()
   * @see #pulse()
   */
  public UpdateState alert() {
    updateLight.setAlert(new Alert().setAction(AlertType.BREATHE));
    return this;
  }

  /**
   * Alias for {@link #alert()} - flashes the light a few times.
   * Uses the Philips Hue "breathe" alert which creates a pulsing flash effect.
   *
   * @return This state, for easy chaining of different methods.
   * @see #alert()
   * @see #pulse()
   */
  public UpdateState flash() {
    return alert();
  }

  /**
   * Alias for {@link #alert()} - creates a pulsing effect on the light.
   * Uses the Philips Hue "breathe" alert which creates a gentle pulsing effect.
   *
   * @return This state, for easy chaining of different methods.
   * @see #alert()
   * @see #flash()
   */
  public UpdateState pulse() {
    return alert();
  }

  /**
   * <p>Starts the timed sunrise effect. The maximum duration is six hours.
   * The timed effect may be stopped with the {@link #clearTimedEffect()} method.</p>
   *
   * <p>Note that not all lights, not even all the color ones, support timed effects.
   * Also note that rooms and zones do not support timed effects.</p>
   *
   * @param duration Duration of the sunrise effect.
   * @return This state, for easy chaining of different methods.
   * @see #clearTimedEffect()
   */
  public UpdateState sunrise(final Duration duration) {
    timedEffect(TimedEffectType.SUNRISE, duration);
    return this;
  }

  /**
   * <p>Stops the timed effect.</p>
   *
   * <p>Note that not all lights, not even all the color ones, support timed effects.
   * Also note that rooms and zones do not support timed effects.</p>
   *
   * @return This state, for easy chaining of different methods.
   * @see #sunrise(Duration)
   */
  public UpdateState clearTimedEffect() {
    timedEffect(TimedEffectType.NO_EFFECT, null);
    return this;
  }

  /**
   * <p>Starts a timed effect. As of December 2022, "sunrise" and "no effect" are the only two options
   * that the Bridge offers. There are helper methods for both of those in this class, but this method
   * exists here for extra convenience so that you can start and stop the effect with the same method,
   * just changing the parameters.</p>
   *
   * <p>Note that not all lights, not even all the color ones, support timed effects.
   * Also note that rooms and zones do not support timed effects.</p>
   *
   * @param effect Type of the effect.
   * @param duration How long should the effect last.
   * @return This state, for easy chaining of different methods.
   * @see #sunrise(Duration)
   * @see #clearTimedEffect()
   */
  public UpdateState timedEffect(final TimedEffectType effect, final Duration duration) {
    updateLight.setTimedEffects(new TimedEffects().setDuration(duration).setEffect(effect));
    return this;
  }

  /**
   * Gradient color setting, for lights that support gradients.
   * There must be exactly two, three, four, or five colors in the list.
   *
   * @param colors A List of {@link io.github.greenstevester.heuvana.Color} objects to specify the colors.
   *               List length must be between 2 and 5, inclusive.
   * @return This state, for easy chaining of different methods.
   */
  public UpdateState gradient(final List<io.github.greenstevester.heuvana.Color> colors) {
    final List<GradientPoint> gradientPoints = colors.stream()
        .map(color -> {
          final XAndYAndBrightness xy = XAndYAndBrightness.rgbToXy(color);
          return new Color()
              .setXy(new Xy().setX(xy.getX()).setY(xy.getY()));
        })
        .map(color -> new GradientPoint().setColor(color))
        .collect(Collectors.toList());
    updateLight.setGradient(new Gradient().setPoints(gradientPoints));
    return this;
  }

  /**
   * Sets the transition time for this state change.
   * This controls how long the light takes to transition to the new state.
   *
   * @param duration The transition duration (will be converted to milliseconds).
   * @return This state, for easy chaining of different methods.
   * @throws IllegalArgumentException if duration is null, negative, or exceeds maximum allowed value
   */
  public UpdateState transitionTime(final Duration duration) {
    if (duration == null) {
      throw new IllegalArgumentException("Duration must not be null");
    }
    if (duration.isNegative()) {
      throw new IllegalArgumentException("Duration must not be negative");
    }

    final long millis = duration.toMillis();
    if (millis > Integer.MAX_VALUE) {
      throw new IllegalArgumentException(
          "Duration too long (maximum " + Integer.MAX_VALUE + " milliseconds)");
    }

    updateLight.setDynamics(new io.github.greenstevester.heuvana.v2.domain.update.Dynamics()
        .setDuration((int) millis));
    return this;
  }

  UpdateLight getUpdateLight() {
    return updateLight;
  }
}
