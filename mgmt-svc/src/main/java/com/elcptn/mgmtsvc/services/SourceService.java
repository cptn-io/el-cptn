package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Source;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/7/23 */

@Service
@RequiredArgsConstructor
public class SourceService extends CommonService {

    private final SourceRepository sourceRepository;

    @CachePut(value = "sources", key = "#source.id")
    public Source create(Source source) {

        if (source.getSecured()) {
            source.setupNewKeys();
        }
        return sourceRepository.save(source);
    }

    @Cacheable(value = "sources", key = "#id")
    public Optional<Source> getById(UUID id) {
        return sourceRepository.findById(id);
    }

    @CachePut(value = "sources", key = "#source.id")
    public Source update(Source source) {
        if (source.getSecured() && !source.hasAnyKeysSetup()) {
            source.setupNewKeys();
        }
        return sourceRepository.save(source);
    }

    @CachePut(value = "sources", key = "#source.id")
    public Source rotateKeys(Source source) {
        source.rotateKeys();
        return sourceRepository.save(source);
    }

    public List<Source> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return sourceRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public long count() {
        return sourceRepository.count();
    }

    @CacheEvict(value = "sources", key = "#source.id")
    public void delete(Source source) {
        sourceRepository.delete(source);
    }
}
