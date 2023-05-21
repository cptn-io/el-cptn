package io.cptn.common.listeners;

import io.cptn.common.entities.BaseEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;

/* @author: kc, created on 2/7/23 */

public class EntityListener {

    @PrePersist
    public void preCreate(BaseEntity baseEntity) {

        //get current principal from securityContext
        String username = getCurrentUser();

        ZonedDateTime timeNow = ZonedDateTime.now();
        baseEntity.setCreatedAt(timeNow);
        baseEntity.setUpdatedAt(timeNow);
        baseEntity.setCreatedBy(username);
        baseEntity.setUpdatedBy(username);
    }

    @PreUpdate
    public void preUpdate(BaseEntity baseEntity) {
        ZonedDateTime timeNow = ZonedDateTime.now();
        baseEntity.setUpdatedAt(timeNow);
        baseEntity.setUpdatedBy(getCurrentUser());
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails userDetails) {

            return userDetails.getUsername();
        } else {
            return "system";
        }
    }
}
