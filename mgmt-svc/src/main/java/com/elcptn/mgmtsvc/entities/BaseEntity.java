package com.elcptn.mgmtsvc.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@MappedSuperclass
@EntityListeners(EntityListener.class)
public class BaseEntity {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Getter
    @Version
    private int version;

    @Getter
    @Setter
    @Column(columnDefinition = "timestamp with time zone", updatable = false)
    private ZonedDateTime createdAt;

    @Getter
    @Setter
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime updatedAt;

    @Getter
    @Setter
    @Column(length = 36, updatable = false)
    private String createdBy;

    @Getter
    @Setter
    @Column(length = 36)
    private String updatedBy;
}
