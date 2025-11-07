package io.github.greenstevester.heuvana.v2.domain.update;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum EffectType {
  /**
   * An effect not supported by this version of this library.
   */
  @JsonEnumDefaultValue UNKNOWN,

  @JsonProperty("fire") FIRE,
  @JsonProperty("candle") CANDLE,
  @JsonProperty("sparkle") SPARKLE,
  @JsonProperty("prism") PRISM,
  @JsonProperty("opal") OPAL,
  @JsonProperty("glisten") GLISTEN,
  @JsonProperty("underwater") UNDERWATER,
  @JsonProperty("cosmos") COSMOS,
  @JsonProperty("sunbeam") SUNBEAM,
  @JsonProperty("enchant") ENCHANT,
  @JsonProperty("no_effect") NO_EFFECT;
}
