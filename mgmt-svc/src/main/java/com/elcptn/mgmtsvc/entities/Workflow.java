package com.elcptn.mgmtsvc.entities;

import com.elcptn.mgmtsvc.helpers.StringHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.ZonedDateTime;
import java.util.Objects;

/* @author: kc, created on 2/7/23 */

@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Workflow extends BaseEntity {

    @Getter
    @Setter
    @Column(length = 128)
    @ToString.Include
    private String name;

    @Getter
    @Setter
    private Boolean secured = true;

    @Getter
    @Setter
    private Boolean active = true;

    @Getter
    @Column(length = 16)
    private String primaryKey;

    @Getter
    @Column(length = 16)
    private String secondaryKey;

    @Getter
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime lastKeyRotationAt;

    public void rotateKeys() {
        String newKey = StringHelper.getSecureRandomString(16);
        this.secondaryKey = this.primaryKey;
        this.primaryKey = newKey;
        this.lastKeyRotationAt = ZonedDateTime.now();
    }

    public void setupNewKeys() {
        this.primaryKey = StringHelper.getSecureRandomString(16);
        this.secondaryKey = StringHelper.getSecureRandomString(16);
        this.lastKeyRotationAt = ZonedDateTime.now();
    }

    public boolean hasAnyKeysSetup() {
        return primaryKey != null || secondaryKey != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Workflow workflow = (Workflow) o;
        return getId() != null && Objects.equals(getId(), workflow.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
