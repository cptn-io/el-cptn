package io.cptn.mgmtsvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/* @author: kc, created on 5/10/23 */
@Controller
public class WebController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false, defaultValue = "false") String sso) {
        if ("true".equals(sso)) {
            return "redirect:/oauth2/authorization/oidc";
        }

        return "redirect:/signin";
    }
}
