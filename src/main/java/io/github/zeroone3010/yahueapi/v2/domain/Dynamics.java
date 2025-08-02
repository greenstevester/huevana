package io.github.greenstevester.yahueapi.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Dynamics {

  @JsonProperty("speed")
  private float speed;

  @JsonProperty("status")
  private String status;

  @JsonProperty("status_values")
  private List<String> statusValues;

  @JsonProperty("speed_valid")
  private boolean speedValid;

  @Override
  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
