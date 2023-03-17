package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.ToString;

/* @author: kc, created on 3/9/23 */
@Entity
@Table(name = "outbound_queue")
@ToString(onlyExplicitlyIncluded = true)
public class OutboundEvent extends BaseOutboundEvent {

}
