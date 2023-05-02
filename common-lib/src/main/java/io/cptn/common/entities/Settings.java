package io.cptn.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

/* @author: kc, created on 5/1/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Settings {

    @Id
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private String key;

    @Setter
    @Getter
    @Column(length = 1024)
    private String value;

    @Setter
    @Getter
    private Boolean systemManaged = false;
}
