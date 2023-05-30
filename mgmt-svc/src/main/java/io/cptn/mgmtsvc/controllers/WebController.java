package io.cptn.mgmtsvc.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cptn.common.helpers.JsonHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/api/version")
    public ResponseEntity<JsonNode> getVersion(@Value("${app.version}") String version) {
        ObjectMapper mapper = JsonHelper.getMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("version", version);

        return ResponseEntity.ok(objectNode);
    }
}
