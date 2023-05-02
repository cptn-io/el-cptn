package io.cptn.ingestionsvc.services;

import io.cptn.common.entities.Source;
import io.cptn.common.repositories.SourceRepository;
import io.cptn.common.services.CommonService;
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
