package io.cptn.mgmtsvc.controllers;

import io.cptn.common.entities.SSOProfile;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.validation.OnCreate;
import io.cptn.common.validation.OnUpdate;
import io.cptn.mgmtsvc.dto.SSOProfileDto;
import io.cptn.mgmtsvc.mappers.SSOProfileMapper;
import io.cptn.mgmtsvc.services.SSOProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/* @author: kc, created on 5/9/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SSOProfileController {

    private final SSOProfileService ssoProfileService;

    private final SSOProfileMapper mapper;

    @PostMapping("/api/ssoProfile")
    public ResponseEntity<SSOProfileDto> create(@Validated(OnCreate.class) @RequestBody SSOProfileDto ssoProfileDto,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        SSOProfile ssoProfile = mapper.toEntity(ssoProfileDto);

        return ResponseEntity.ok(mapper.toDto(ssoProfileService.upsert(ssoProfile)));
    }

    @PutMapping("/api/ssoProfile")
    public ResponseEntity<SSOProfileDto> update(@Validated(OnUpdate.class) @RequestBody SSOProfileDto ssoProfileDto) {
        SSOProfile ssoProfile = mapper.toEntity(ssoProfileDto);

        return ResponseEntity.ok(mapper.toDto(ssoProfileService.upsert(ssoProfile)));
    }

    @GetMapping("/api/ssoProfile")
    public ResponseEntity<SSOProfileDto> get() {
        SSOProfile ssoProfile = ssoProfileService.getSSOProfile();
        if (ssoProfile == null) {
            throw new NotFoundException("SSO Profile not found");
        }
        return ResponseEntity.ok(mapper.toDto(ssoProfile));
    }

    @DeleteMapping("/api/ssoProfile")
    public ResponseEntity<Void> delete() {
        ssoProfileService.delete();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/checksso")
    public ResponseEntity<ResponseEntity> checkIfSSOIsEnabled() {
        SSOProfile ssoProfile = ssoProfileService.getSSOProfile();

        if (ssoProfile == null || !ssoProfile.getActive()) {
            throw new NotFoundException("SSO is not enabled");
        }
        return ResponseEntity.ok().build();
    }
}
