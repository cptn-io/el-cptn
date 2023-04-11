package com.elcptn.mgmtsvc.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/* @author: kc, created on 4/11/23 */
@RestController
public class CsrfController {

    private CsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();

    @GetMapping("/api/csrf")
    public CsrfToken getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrfToken, request, response);
        return csrfToken;
    }

}

