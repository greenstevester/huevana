package io.github.greenstevester.yahueapi.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemperatureResource extends Resource {
  @JsonProperty("enabled")
  private boolean enabled;

  @JsonProperty("temperature")
  private Temperature temperature;

  public boolean isEnabled() {
    return enabled;
  }

  public Temperature getTemperature() {
    return temperature;
  }

  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
