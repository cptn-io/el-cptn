package com.elcptn.mgmtsvc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/* @author: kc, created on 5/1/23 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class SettingsDto {

    @EqualsAndHashCode.Include
    private String key;

    private String value;
}
