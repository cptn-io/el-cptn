package com.elcptn.mgmtsvc.dto;

import lombok.Data;

/* @author: kc, created on 3/27/23 */
@Data
public class ConfigItemDto {

    private String key;

    private String value;

    private boolean secret;
}
