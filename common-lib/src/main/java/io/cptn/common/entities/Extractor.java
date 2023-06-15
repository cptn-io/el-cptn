package io.cptn.common.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.util.UUID;

/* @author: kc, created on 6/12/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Extractor extends ScriptedStep {
    @Serial
    private static final long serialVersionUID = -4475166215962430482L;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Source source;

    public Extractor(UUID id) {
        this.setId(id);
    }
}
