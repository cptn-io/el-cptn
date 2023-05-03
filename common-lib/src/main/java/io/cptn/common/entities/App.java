package io.cptn.common.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cptn.common.listeners.ConfigConverter;
import io.cptn.common.pojos.ConfigItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

/* @author: kc, created on 4/25/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class App extends BaseEntity {

    @Getter
    @Setter
    @Column(length = 32, unique = true, nullable = false)
    private String key;

    @Getter
    @Setter
    @Column(length = 100, nullable = false)
    private String name;

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "script")
    private String script;

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Convert(converter = ConfigConverter.class)
    private List<ConfigItem> config;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false, columnDefinition = "varchar(32) default 'DESTINATION'")
    private AppType type = AppType.DESTINATION;

    @Getter
    @Setter
    @Column(length = 32, nullable = false)
    private String hash;

    @Getter
    @Setter
    private String logoUrl;
}
