package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Source;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/* @author: kc, created on 2/7/23 */

@Service
@RequiredArgsConstructor
public class SourceService extends CommonService {

    private final SourceRepository sourceRepository;

    public Source create(Source source) {

        if (source.getSecured()) {
            source.setupNewKeys();
        }
        return sourceRepository.save(source);
    }

    public Optional<Source> getById(UUID id) {
        return sourceRepository.findById(id);
    }

    public Source update(Source source) {
        if (source.getSecured() && !source.hasAnyKeysSetup()) {
            source.setupNewKeys();
        }
        return sourceRepository.save(source);
    }

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

    public void delete(Source source) {
        sourceRepository.delete(source);
    }

    public Set<UUID> getInvalidSources(Set<Source> sources) {
        if (sources == null) {
            return new HashSet<>();
        }

        Set<UUID> invalidIds = new HashSet<>();
        for (Source wf : sources) {
            if (getById(wf.getId()).isEmpty()) {
                invalidIds.add(wf.getId());
            }
        }
        return invalidIds;
    }
}
