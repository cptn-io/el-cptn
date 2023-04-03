package com.elcptn.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@Data
public abstract class BaseDto implements Serializable {

    private UUID id;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
