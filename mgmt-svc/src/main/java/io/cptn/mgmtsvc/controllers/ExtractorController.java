package io.cptn.mgmtsvc.controllers;

import io.cptn.common.entities.Extractor;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.validation.OnCreate;
import io.cptn.mgmtsvc.dto.ExtractorDto;
import io.cptn.mgmtsvc.mappers.ExtractorMapper;
import io.cptn.mgmtsvc.services.ExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/* @author: kc, created on 6/14/23 */
@RestController
@RequiredArgsConstructor
public class ExtractorController {

    private final ExtractorService extractorService;

    private final ExtractorMapper mapper;

    @PostMapping("/api/extractor")
    public ResponseEntity<ExtractorDto> create(@Validated(OnCreate.class) @RequestBody ExtractorDto extractorDto,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Extractor extractor = extractorService.create(convert(extractorDto));
        return ResponseEntity.ok(convert(extractor));
    }

    private Extractor convert(ExtractorDto extractorDto) {
        return mapper.toEntity(extractorDto);
    }

    private ExtractorDto convert(Extractor extractor) {
        return mapper.toDto(extractor);
    }

}
