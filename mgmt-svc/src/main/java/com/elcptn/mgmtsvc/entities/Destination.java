package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/* @author: kc, created on 2/15/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Destination extends BaseEntity {

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
