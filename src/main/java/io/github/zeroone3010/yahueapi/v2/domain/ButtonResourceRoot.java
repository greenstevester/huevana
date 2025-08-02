package io.github.greenstevester.yahueapi.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ButtonResourceRoot {

  @JsonProperty("errors")
  private List<Error> errors;

  @JsonProperty("data")
  private List<ButtonResource> data;

  public List<Error> getErrors() {
    return errors;
  }

  public List<ButtonResource> getData() {
    return data;
  }

  @Override
  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
