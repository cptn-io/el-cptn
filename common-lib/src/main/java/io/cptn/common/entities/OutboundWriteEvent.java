package io.cptn.common.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;

/* @author: kc, created on 3/9/23 */
@Entity
@Table(name = "outbound_write_queue")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class OutboundWriteEvent extends BaseOutboundEvent {
    @Serial
    private static final long serialVersionUID = -2615881382902684770L;
}
