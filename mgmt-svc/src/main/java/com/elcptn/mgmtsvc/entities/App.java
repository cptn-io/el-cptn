package com.elcptn.mgmtsvc.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

/* @author: kc, created on 2/9/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true)
public class App extends BaseEntity {

    @Getter
    @Setter
    @Column(length = 100)
    private String name;
}
