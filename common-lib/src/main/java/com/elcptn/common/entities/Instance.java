package com.elcptn.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/* @author: kc, created on 4/24/23 */
@Entity
public class Instance extends BaseEntity {

    @Getter
    @Setter
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime registeredUntil;

    @Getter
    @Setter
    @Column(length = 128)
    private String companyName;

    @Getter
    @Setter
    @Column(length = 128)
    private String primaryEmail;

    @Getter
    @Setter
    @Column(length = 128)
    private String secondaryEmail;

    @Getter
    @Setter
    @Column(length = 1024)
    private String token;

    @Getter
    @Setter
    private Boolean acceptedTerms;

    @Getter
    @Setter
    private Boolean collectStats;

}
