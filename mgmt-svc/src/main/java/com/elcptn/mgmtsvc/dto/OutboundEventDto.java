package com.elcptn.mgmtsvc.dto;

import com.elcptn.common.dto.BaseDto;
import com.elcptn.common.entities.State;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* @author: kc, created on 4/3/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundEventDto extends BaseDto {

    private JsonNode payload;

    private State state = State.QUEUED;

    private String exception;
}
