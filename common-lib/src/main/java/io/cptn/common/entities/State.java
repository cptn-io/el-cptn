package io.cptn.common.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/* @author: kc, created on 2/27/23 */
public enum State {
    @JsonProperty("QUEUED")
    QUEUED,
    @JsonProperty("IN_PROGRESS")
    IN_PROGRESS,
    @JsonProperty("COMPLETED")
    COMPLETED,
    @JsonProperty("FAILED")
    FAILED
}
