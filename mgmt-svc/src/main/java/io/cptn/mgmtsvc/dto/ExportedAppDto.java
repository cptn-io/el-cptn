package io.cptn.mgmtsvc.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/* @author: kc, created on 5/15/23 */
@Data
public class ExportedAppDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 3209837909087365228L;

    private String key;

    private String script;

    private String name;

    private String hash;

    private String type;

    private List<ConfigItemDto> config;

    private String logoUrl;

    private int orderIndex;
}
