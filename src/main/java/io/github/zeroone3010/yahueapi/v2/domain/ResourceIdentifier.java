package io.github.greenstevester.yahueapi.v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ResourceIdentifier {

  private UUID rid;

  private ResourceType rtype;

  @JsonProperty("rid")
  public UUID getResourceId() {
    return rid;
  }

  @JsonProperty("rid")
  public void setResourceId(final UUID rid) {
    this.rid = rid;
  }

  @JsonProperty("rtype")
  public ResourceType getResourceType() {
    return rtype;
  }

  @JsonProperty("rtype")
  public void setResourceType(final ResourceType rtype) {
    this.rtype = rtype;
  }

  @Override
  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
