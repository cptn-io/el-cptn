package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/* @author: kc, created on 3/9/23 */
@Entity
@Table(name = "outbound_queue")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
public class OutboundEvent extends BaseOutboundEvent {
    @Serial
    private static final long serialVersionUID = -6768974551952290663L;
}
