package com.elcptn.ingestionsvc.services;

import com.elcptn.common.entities.Source;
import com.elcptn.common.repositories.SourceRepository;
import com.elcptn.common.services.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@Service
@RequiredArgsConstructor
public class SourceService extends CommonService {

    private final SourceRepository sourceRepository;

    @Cacheable(value = "sources", key = "#id")
    public Optional<Source> getById(UUID id) {
        return sourceRepository.findById(id);
    }

}
