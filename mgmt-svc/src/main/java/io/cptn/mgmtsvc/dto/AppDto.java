package io.cptn.mgmtsvc.dto;

import io.cptn.common.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/* @author: kc, created on 4/25/23 */
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class AppDto extends BaseDto {

    @Serial
    private static final long serialVersionUID = 7778089433386136027L;

    private String name;

    private String key;

    private List<ConfigItemDto> config;

    private String script;

    private String type;

    private String logoUrl;
}
