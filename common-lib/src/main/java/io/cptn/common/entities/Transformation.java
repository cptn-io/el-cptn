package io.cptn.common.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/* @author: kc, created on 2/15/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Transformation extends ScriptedStep {

    @Serial
    private static final long serialVersionUID = -5254605850932584190L;

    @ManyToMany(mappedBy = "transformations", fetch = FetchType.LAZY)
    private Set<Pipeline> pipelines = new HashSet<>();

    public Transformation(UUID id) {
        this.setId(id);
    }
}
