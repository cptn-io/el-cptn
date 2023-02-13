package com.elcptn.mgmtsvc.entities;

/* @author: kc, created on 2/9/23 */

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uc_operationId_opVersion", columnNames = {"operationId", "opVersion"})
})
public class Operation extends BaseEntity {

    @Getter
    @Setter
    @Column(length = 36, updatable = false)
    private String operationId;

    @Getter
    @Setter
    @Column(length = 100)
    private String name;

    @Getter
    @Setter
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "script")
    private String script;

    @Getter
    @Setter
    private OperationType type;

    @Getter
    @Setter
    private Integer opVersion;

    @Getter
    @Setter
    @Column(length = 32)
    private String scriptHash;

    @Getter
    @Setter
    private Boolean locked = false;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private App app;

}
