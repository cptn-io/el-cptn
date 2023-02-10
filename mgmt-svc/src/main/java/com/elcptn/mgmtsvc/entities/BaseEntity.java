package com.elcptn.mgmtsvc.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@MappedSuperclass
@EntityListeners(EntityListener.class)
@ToString(onlyExplicitlyIncluded = true)
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class BaseEntity {

    @Id
    @Getter
    @Setter
    @Column(columnDefinition = "uuid", updatable = false)
    @ToString.Include
    private UUID id = UUID.randomUUID();

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
