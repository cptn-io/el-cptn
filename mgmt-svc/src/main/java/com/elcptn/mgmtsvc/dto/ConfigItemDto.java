package com.elcptn.mgmtsvc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class ConfigItemDto {

    private String key;

    private String value;

    private boolean secret;
}
