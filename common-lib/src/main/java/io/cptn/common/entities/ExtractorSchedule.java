package io.cptn.common.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;

/* @author: kc, created on 4/4/23 */
@Entity
@Table(name = "extractor_schedule")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ExtractorSchedule extends BaseSchedule {

    @Serial
    private static final long serialVersionUID = -2844066157978123013L;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extractor_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Extractor extractor;
}
