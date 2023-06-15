package io.cptn.common.entities;

import jakarta.persistence.*;
import lombok.*;

/* @author: kc, created on 3/29/23 */
@Entity
@Table(name = "extractor_trigger")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ExtractorTrigger extends BaseEntity {

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extractor_id")
    private Extractor extractor;

    @Enumerated(EnumType.STRING)
    @Setter
    @Getter
    @Column(length = 25)
    private State state = State.QUEUED;
}
