package com.elcptn.common.pojos;

import com.elcptn.common.entities.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/* @author: kc, created on 3/9/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusMetric implements Serializable {

    @Serial
    private static final long serialVersionUID = 5225093520094860293L;
    private State state;

    private Long count;
}
