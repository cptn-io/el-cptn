package com.elcptn.mgmtsvc.controllers;

import com.elcptn.common.entities.Settings;
import com.elcptn.common.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.dto.SettingsDto;
import com.elcptn.mgmtsvc.mappers.SettingsMapper;
import com.elcptn.mgmtsvc.services.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/* @author: kc, created on 5/1/23 */
@RestController
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    private final SettingsMapper mapper;

    @PostMapping("/api/settings")
    public ResponseEntity<SettingsDto> upsertSettings(@RequestBody SettingsDto settingsDto) {
        Settings settings = settingsService.upsert(settingsDto.getKey(), settingsDto.getValue(), false);
        return ResponseEntity.ok(mapper.toDto(settings));
    }

    @PostMapping("/api/settings/{key}")
    public ResponseEntity<SettingsDto> getSettings(@PathVariable String key) {
        Settings settings = settingsService.get(key);
        if (settings == null) {
            throw new NotFoundException("Settings not found for key=" + key);
        }
        return ResponseEntity.ok(mapper.toDto(settings));
    }

    @DeleteMapping("/api/settings/{key}")
    public ResponseEntity deleteSettings(@PathVariable String key) {
        settingsService.delete(key);
        return ResponseEntity.notFound().build();
    }
}
