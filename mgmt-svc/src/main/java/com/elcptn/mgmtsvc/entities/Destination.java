package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.Entity;
import lombok.ToString;

import java.io.Serial;

/* @author: kc, created on 2/15/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Destination extends ScriptedStep {
    @Serial
    private static final long serialVersionUID = 514405197195059184L;
}
