package com.elcptn.common.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.Set;

/* @author: kc, created on 3/7/23 */
@Entity
@Table(name = "pipeline")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Pipeline extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 142919398453772084L;

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

    @Getter
    @Setter
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "pipeline_transformation",
            joinColumns = @JoinColumn(name = "pipeline_id"),
            inverseJoinColumns = @JoinColumn(name = "transformation_id"))
    private Set<Transformation> transformations = Sets.newHashSet(); //use Set for many-to-many for optimized deletes

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode transformationMap;

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode route;

    @Getter
    @Setter
    @Column(name = "batch_process")
    private Boolean batchProcess = false;

    public void addTransformation(Transformation transformation) {
        transformations.add(transformation);
    }
}
