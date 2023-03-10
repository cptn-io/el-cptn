package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/* @author: kc, created on 3/9/23 */
@Entity
@Table(name = "outbound_write_queue")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class OutboundWriteEvent extends BaseOutboundQueue {

    public OutboundWriteEvent(Event event, UUID pipelineId) {
        this.setPayload(event.getPayload());
        this.setPipeline(new Pipeline(pipelineId));
    }
}
