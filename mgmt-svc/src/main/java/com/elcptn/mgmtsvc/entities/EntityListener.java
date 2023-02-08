package com.elcptn.mgmtsvc.entities;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZonedDateTime;

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
