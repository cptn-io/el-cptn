package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.repositories.DBMaintenanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/* @author: kc, created on 3/9/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class MaintenanceController {

    private final DBMaintenanceRepository dbMaintenanceRepository;

    @PostMapping("/api/maint/outbound")
    public ResponseEntity rotateTables() {
        dbMaintenanceRepository.rotateOutboundQueues();
        return ResponseEntity.ok("ok");
    }
}
