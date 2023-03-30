package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/* @author: kc, created on 3/29/23 */
@Entity
@Table(name = "pipeline_trigger")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class PipelineTrigger extends BaseEntity {

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
}
