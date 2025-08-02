package io.github.greenstevester.yahueapi.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LightResourceRoot {

  @JsonProperty("errors")
  private List<Error> errors;

  @JsonProperty("data")
  private List<LightResource> data;

  public List<Error> getErrors() {
    return errors;
  }

  public List<LightResource> getData() {
    return data;
  }

  @Override
  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
