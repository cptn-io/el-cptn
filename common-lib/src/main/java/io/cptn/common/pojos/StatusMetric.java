package io.cptn.common.pojos;

import io.cptn.common.entities.State;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/* @author: kc, created on 3/9/23 */
@Data
@NoArgsConstructor
public class StatusMetric implements Serializable {

    @Serial
    private static final long serialVersionUID = 5225093520094860293L;

    private State state;

    private Long count;

    public StatusMetric(State state, Long count) {
        this.state = state;
        this.count = count;
    }
}
