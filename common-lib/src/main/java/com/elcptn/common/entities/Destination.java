package com.elcptn.common.entities;

import com.elcptn.common.listeners.ConfigConverter;
import com.elcptn.common.pojos.ConfigItem;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

/* @author: kc, created on 2/15/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
public class Destination extends ScriptedStep {
    @Serial
    private static final long serialVersionUID = 514405197195059184L;

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Convert(converter = ConfigConverter.class)
    @EqualsAndHashCode.Include
    private List<ConfigItem> config;

    public Destination(UUID id) {
        this.setId(id);
    }
}
