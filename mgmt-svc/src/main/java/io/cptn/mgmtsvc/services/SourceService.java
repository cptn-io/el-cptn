package io.cptn.mgmtsvc.services;

import com.querydsl.core.types.Predicate;
import io.cptn.common.entities.Source;
import io.cptn.common.repositories.SourceRepository;
import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@Service
@RequiredArgsConstructor
public class SourceService extends CommonService {

    private final SourceRepository sourceRepository;

    @CachePut(value = "sources", key = "#source.id")
    public Source create(Source source) {

        if (Boolean.TRUE.equals(source.getSecured())) {
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
        if (Boolean.TRUE.equals(source.getSecured()) && !source.hasAnyKeysSetup()) {
            source.setupNewKeys();
        }
        return sourceRepository.save(source);
    }

    @CachePut(value = "sources", key = "#source.id")
    public Source rotateKeys(Source source) {
        source.rotateKeys();
        return sourceRepository.save(source);
    }

    public List<Source> getAll(ListEntitiesParam param, Predicate predicate) {
        Pageable pageable = getPageable(param);

        if (predicate == null) {
            return sourceRepository.findAll(pageable).stream().toList();
        } else {
            return sourceRepository.findAll(predicate, pageable).stream().toList();
        }
    }

    public long count() {
        return sourceRepository.count();
    }

    public long count(Predicate predicate) {
        if (predicate == null) {
            return count();
        } else {
            return sourceRepository.count(predicate);
        }
    }

    @CacheEvict(value = "sources", key = "#source.id")
    public void delete(Source source) {
        sourceRepository.delete(source);
    }


}
