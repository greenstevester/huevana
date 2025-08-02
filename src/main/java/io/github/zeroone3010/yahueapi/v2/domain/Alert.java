package io.github.greenstevester.yahueapi.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Alert {

  @JsonProperty("action_values")
  private List<String> actionValues;

  public List<String> getActionValues() {
    return actionValues;
  }

  @Override
  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
