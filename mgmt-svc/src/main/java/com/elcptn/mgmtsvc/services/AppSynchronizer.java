package com.elcptn.mgmtsvc.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/* @author: kc, created on 4/27/23 */
@Component
public class AppSynchronizer {

    @Value("${app.repo.url:https://github.com/cptn-io/apps.git}")
    private String repoUrl;

    private void cloneRepository() {

    }
}
