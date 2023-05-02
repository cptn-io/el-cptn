package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.cptn.common.dto.BaseDto;
import io.cptn.common.entities.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/* @author: kc, created on 4/3/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class OutboundEventDto extends BaseDto {

    private JsonNode payload;

    private State state = State.QUEUED;

    private String consoleLog;
}
