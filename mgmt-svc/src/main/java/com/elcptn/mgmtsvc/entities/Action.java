package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

/* @author: kc, created on 2/15/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Action extends BaseEntity {

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

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "source_action_map",
            joinColumns = {@JoinColumn(name = "action_id")},
            inverseJoinColumns = {@JoinColumn(name = "source_id")}
    )
    private Set<Source> sources = new HashSet<>();

}
