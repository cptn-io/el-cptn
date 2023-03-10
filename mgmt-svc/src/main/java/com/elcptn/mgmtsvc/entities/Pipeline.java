package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

/* @author: kc, created on 3/7/23 */
@Entity
@Table(name = "pipeline")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Pipeline extends BaseEntity {

    @Getter
    @Setter
    @Column(length = 128)
    @ToString.Include
    private String name;

    @Getter
    @Setter
    private Boolean active = true;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Source source;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Destination destination;

    public Pipeline(UUID id) {
        this.setId(id);
    }
}
