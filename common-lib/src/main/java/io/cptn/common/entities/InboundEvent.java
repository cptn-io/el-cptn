package io.cptn.common.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/* @author: kc, created on 2/8/23 */
@Entity
@Table(name = "inbound_queue")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class InboundEvent extends BaseInboundEvent {

    @Serial
    private static final long serialVersionUID = -560620717893911175L;

}
