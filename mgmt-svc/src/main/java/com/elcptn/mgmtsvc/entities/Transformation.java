package com.elcptn.mgmtsvc.entities;

import jakarta.persistence.Entity;
import lombok.ToString;

/* @author: kc, created on 2/15/23 */
@Entity
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Transformation extends ScriptedStep {

}
