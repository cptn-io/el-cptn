package com.elcptn.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;

/* @author: kc, created on 3/17/23 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ScriptedStep extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8585075555944933505L;
    @Getter
    @Setter
    @Column(length = 100)
    private String name;

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "script")
    private String script;

    @Getter
    @Setter
    private Boolean active = true;


}
