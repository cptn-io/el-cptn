package com.elcptn.mgmtsvc.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class BaseDto {

    private String id;

    private ZonedDateTime createdAt;
    
    private ZonedDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
