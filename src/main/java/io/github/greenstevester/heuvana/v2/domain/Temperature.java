package io.github.greenstevester.heuvana.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Temperature {
  @JsonProperty("temperature")
  private BigDecimal temperature;

  @JsonProperty("temperature_valid")
  private boolean temperatureValid;

  @JsonProperty("temperature_report")
  private TemperatureReport temperatureReport;

  public BigDecimal getTemperature() {
    return temperature;
  }

  public boolean isTemperatureValid() {
    return temperatureValid;
  }

  public TemperatureReport getTemperatureReport() {
    return temperatureReport;
  }

  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
