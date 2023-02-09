package com.elcptn.mgmtsvc.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

/* @author: kc, created on 2/7/23 */

@Data
public class BaseDto implements Serializable {

    private String id;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
