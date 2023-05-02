package io.cptn.mgmtsvc.dto;

import lombok.Data;

import java.io.Serializable;

/* @author: kc, created on 4/25/23 */
@Data
public class InstanceDto implements Serializable {

    private static final long serialVersionUID = -183566405534637678L;

    private String id;

    private String primaryEmail;

    private String secondaryEmail;

    private String companyName;

    private String token;

    private Boolean acceptedTerms;

    private Boolean collectStats;
}
