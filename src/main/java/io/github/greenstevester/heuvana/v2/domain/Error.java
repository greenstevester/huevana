package io.github.greenstevester.heuvana.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {
  @JsonProperty("description")
  private String description;

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
