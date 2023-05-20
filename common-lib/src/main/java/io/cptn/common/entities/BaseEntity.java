package io.cptn.common.entities;

import io.cptn.common.listeners.EntityListener;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@MappedSuperclass
@EntityListeners(EntityListener.class)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity implements Serializable {

    @Id
    @Getter
    @Setter
    @Column(columnDefinition = "uuid", updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue
    private UUID id = null;

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
    @ToString.Include
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
