package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/* @author: kc, created on 3/17/23 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ScriptedStep extends BaseEntity {

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
