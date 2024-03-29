package io.cptn.common.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;

/* @author: kc, created on 4/4/23 */
@Entity
@Table(name = "pipeline_schedule")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class PipelineSchedule extends BaseSchedule {

    @Serial
    private static final long serialVersionUID = 142919398453772084L;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Pipeline pipeline;
}
