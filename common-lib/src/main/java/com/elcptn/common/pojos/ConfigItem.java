package com.elcptn.common.pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class ConfigItem {
    
    private String key;

    private String value;

    private boolean secret;
}
