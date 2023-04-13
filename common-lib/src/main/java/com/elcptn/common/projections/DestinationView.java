package com.elcptn.common.projections;

import java.time.ZonedDateTime;
import java.util.UUID;

/* @author: kc, created on 4/13/23 */
public interface DestinationView {

    UUID getId();

    String getName();

    Boolean getActive();

    ZonedDateTime getCreatedAt();

    ZonedDateTime getUpdatedAt();
}
