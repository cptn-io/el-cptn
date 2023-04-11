package com.elcptn.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

/* @author: kc, created on 4/10/23 */
@Entity
@Table(name = "cptn_user")
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {


    @Getter
    @Setter
    @Column(length = 128)
    private String firstName;

    @Getter
    @Setter
    @Column(length = 128)
    private String lastName;

    @Getter
    @Setter
    @Column(length = 128)
    private String email;

    @Getter
    @Setter
    private String hashedPassword;

    @Getter
    @Setter
    private boolean disabled;

    @Getter
    @Setter
    private boolean lockedOut;

    @Getter
    @Setter
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime lastLoginAt;

    @Getter
    @Setter
    private boolean mfaEnabled;

    @Getter
    @Setter
    private String mfaKey;
}
