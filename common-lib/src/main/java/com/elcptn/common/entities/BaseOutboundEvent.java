package com.elcptn.common.entities;

/* @author: kc, created on 3/9/23 */

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@MappedSuperclass
public abstract class BaseOutboundEvent extends BaseEntity {

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;

    @Enumerated(EnumType.STRING)
    @Setter
    @Getter
    @Column(length = 25)
    private State state = State.QUEUED;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_event_id")
    private InboundEvent inboundEvent;

    @Getter
    @Setter
    @Column(length = 2000)
    private String exception;

    @Getter
    @Setter
    @Column(length = 4000)
    private String consoleLog;
}
