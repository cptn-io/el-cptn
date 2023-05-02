package io.cptn.common.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/* @author: kc, created on 4/25/23 */
public enum AppType {
    @JsonProperty("DESTINATION")
    DESTINATION,

    @JsonProperty("TRANSFORMATION")
    TRANSFORMATION
}