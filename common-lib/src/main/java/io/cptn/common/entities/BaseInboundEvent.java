package io.cptn.common.entities;

import com.fasterxml.jackson.databind.JsonNode;
import io.cptn.common.helpers.JsonHelper;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;

/* @author: kc, created on 3/30/23 */
@MappedSuperclass
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class BaseInboundEvent extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -7945377903306555486L;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "payload")
    private String payload;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;

    @Enumerated(EnumType.STRING)
    @Setter
    @Getter
    @Column(length = 25)
    private State state = State.QUEUED;


    public JsonNode getPayload() {
        return JsonHelper.deserializeJson(this.payload);
    }

    public void setPayload(JsonNode payload) {
        this.payload = JsonHelper.serializeJson(payload);
    }
}