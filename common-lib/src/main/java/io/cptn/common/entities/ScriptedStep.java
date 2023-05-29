package io.cptn.common.entities;

import io.cptn.common.listeners.ConfigConverter;
import io.cptn.common.pojos.ConfigItem;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.List;

/* @author: kc, created on 3/17/23 */
@MappedSuperclass
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ScriptedStep extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8585075555944933505L;
    @Getter
    @Setter
    @Column(length = 100)
    private String name;

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "script")
    private String script;

    @Getter
    @Setter
    private Boolean active = true;

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Convert(converter = ConfigConverter.class)
    @EqualsAndHashCode.Include
    private List<ConfigItem> config;


}
