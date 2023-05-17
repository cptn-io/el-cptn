package io.cptn.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

/* @author: kc, created on 5/9/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class SSOProfile extends BaseEntity {

    @Getter
    @Setter
    @Column(length = 512)
    private String clientId;

    @Getter
    @Setter
    @Column(length = 512)
    private String clientSecret;

    @Getter
    @Setter
    @Column(length = 4000)
    private String wellKnownUrl;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "BIT(1) default 0")
    private Boolean active = false;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "BIT(1) default 0")
    private Boolean ssoOnly = false;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "BIT(1) default 0")
    private Boolean enableCreateUser = false;

    public void populate(SSOProfile ssoProfile) {
        this.clientId = ssoProfile.getClientId();
        this.clientSecret = ssoProfile.getClientSecret();
        this.wellKnownUrl = ssoProfile.getWellKnownUrl();
        this.active = ssoProfile.getActive();
        this.ssoOnly = ssoProfile.getSsoOnly();
        this.enableCreateUser = ssoProfile.getEnableCreateUser();
    }
}
