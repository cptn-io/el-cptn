package com.elcptn.mgmtsvc.dto;

import com.elcptn.mgmtsvc.entities.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* @author: kc, created on 3/9/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusMetricDto {

    private State state;

    private Long count;
}
