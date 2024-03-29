package io.cptn.mgmtsvc.dto;

import io.cptn.common.entities.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/* @author: kc, created on 3/9/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusMetricDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 5225093520094860293L;
    private State state;

    private Long count;
}
