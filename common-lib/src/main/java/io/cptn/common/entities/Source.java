package io.cptn.common.entities;

import io.cptn.common.helpers.StringHelper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Source extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -3691670877572267385L;

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

    public Source(UUID id) {
        this.setId(id);
    }

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
}
