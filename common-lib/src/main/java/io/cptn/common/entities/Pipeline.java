package io.cptn.common.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import io.cptn.common.helpers.JsonHelper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.Set;
import java.util.UUID;

/* @author: kc, created on 3/7/23 */
@Entity
@Table(name = "pipeline")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String transformationMap;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String route;

    @Getter
    @Setter
    @Column(name = "batch_process")
    private Boolean batchProcess = false;

    public Pipeline(UUID id) {
        this.setId(id);
    }

    public void addTransformation(Transformation transformation) {
        transformations.add(transformation);
    }

    public JsonNode getTransformationMap() {
        return JsonHelper.deserializeJson(this.transformationMap);
    }

    public void setTransformationMap(JsonNode transformationMap) {
        this.transformationMap = JsonHelper.serializeJson(transformationMap);
    }

    public JsonNode getRoute() {
        return JsonHelper.deserializeJson(this.route);
    }

    public void setRoute(JsonNode route) {
        this.route = JsonHelper.serializeJson(route);
    }
}
