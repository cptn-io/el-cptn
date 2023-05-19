package io.cptn.common.entities;

/* @author: kc, created on 3/9/23 */

import com.fasterxml.jackson.databind.JsonNode;
import io.cptn.common.helpers.JsonHelper;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@MappedSuperclass
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class BaseOutboundEvent extends BaseEntity {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

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
    @Column(length = 4000)
    private String consoleLog;

    public JsonNode getPayload() {
        return JsonHelper.deserializeJson(this.payload);
    }

    public void setPayload(JsonNode payload) {
        this.payload = JsonHelper.serializeJson(payload);
    }
}
