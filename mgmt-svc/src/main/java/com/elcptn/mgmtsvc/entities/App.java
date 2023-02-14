package com.elcptn.mgmtsvc.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

/* @author: kc, created on 2/9/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class App extends BaseEntity {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "app")
    @Cascade(CascadeType.DELETE)
    public List<Operation> operations;
    
    @Getter
    @Setter
    @Column(length = 100)
    private String name;

    public App(UUID id) {
        this.setId(id);
    }
}
