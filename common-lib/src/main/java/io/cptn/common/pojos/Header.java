package io.cptn.common.pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/* @author: kc, created on 5/1/23 */
@EqualsAndHashCode
@Data
public class Header implements Serializable {

    @Serial
    private static final long serialVersionUID = 3280946371988838064L;

    private String key;

    private String value;
}
