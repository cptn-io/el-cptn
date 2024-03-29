package io.cptn.common.pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode
@Data
public class ConfigItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1006031220603881842L;

    private String key;

    private String value;

    private boolean secret;
}
