package com.elcptn.mgmtsvc.entities;

import com.elcptn.mgmtsvc.dto.ConfigItemDto;
import com.elcptn.mgmtsvc.listeners.ConfigConverter;
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
@EqualsAndHashCode(callSuper = true)
public class Destination extends ScriptedStep {
    @Serial
    private static final long serialVersionUID = 514405197195059184L;

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Convert(converter = ConfigConverter.class)
    @EqualsAndHashCode.Include
    private List<ConfigItemDto> config;

    public Destination(UUID id) {
        this.setId(id);
    }
}
