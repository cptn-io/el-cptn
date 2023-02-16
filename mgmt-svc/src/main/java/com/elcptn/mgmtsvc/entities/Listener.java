package com.elcptn.mgmtsvc.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/* @author: kc, created on 2/15/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Listener extends BaseEntity {

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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workflow_listener_map",
            joinColumns = {@JoinColumn(name = "workflow_id")},
            inverseJoinColumns = {@JoinColumn(name = "listener_id")}
    )
    private Set<Workflow> workflows = new HashSet<>();

}
