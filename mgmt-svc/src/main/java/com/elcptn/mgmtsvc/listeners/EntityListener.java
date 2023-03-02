package com.elcptn.mgmtsvc.listeners;

import com.elcptn.mgmtsvc.entities.BaseEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.ZonedDateTime;

/* @author: kc, created on 2/7/23 */

public class EntityListener {

    @PrePersist
    public void preCreate(BaseEntity baseEntity) {
        ZonedDateTime timeNow = ZonedDateTime.now();
        baseEntity.setCreatedAt(timeNow);
        baseEntity.setUpdatedAt(timeNow);
    }

    @PreUpdate
    public void preUpdate(BaseEntity baseEntity) {
        ZonedDateTime timeNow = ZonedDateTime.now();
        baseEntity.setUpdatedAt(timeNow);
    }
}
