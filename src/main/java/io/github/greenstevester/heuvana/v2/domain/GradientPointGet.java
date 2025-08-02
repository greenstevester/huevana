package io.github.greenstevester.heuvana.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GradientPointGet {

  @JsonProperty("color")
  private SimpleColor color;

  public SimpleColor getColor() {
    return color;
  }

  @Override
  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
