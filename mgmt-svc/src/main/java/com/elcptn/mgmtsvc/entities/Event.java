package com.elcptn.mgmtsvc.entities;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;
}
