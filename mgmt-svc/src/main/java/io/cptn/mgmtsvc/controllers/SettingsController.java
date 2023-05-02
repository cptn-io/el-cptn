package io.cptn.mgmtsvc.controllers;

import io.cptn.common.entities.Settings;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.mgmtsvc.dto.SettingsDto;
import io.cptn.mgmtsvc.mappers.SettingsMapper;
import io.cptn.mgmtsvc.services.SettingsService;
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
