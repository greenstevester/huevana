package io.github.greenstevester.heuvana.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sensitivity {

  @JsonProperty("status")
  private String status;

  @JsonProperty("sensitivity")
  private int sensitivity;

  @JsonProperty("sensitivity_max")
  private int maxSensitivity;

  public String getStatus() {
    return status;
  }

  public int getSensitivity() {
    return sensitivity;
  }

  public int getSensitivityMax() {
    return maxSensitivity;
  }

  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
