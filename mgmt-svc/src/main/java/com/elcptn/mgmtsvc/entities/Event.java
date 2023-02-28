package com.elcptn.mgmtsvc.entities;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/* @author: kc, created on 2/8/23 */
@Entity
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Event extends BaseEntity {

    @Getter
    @Setter
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

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
}
