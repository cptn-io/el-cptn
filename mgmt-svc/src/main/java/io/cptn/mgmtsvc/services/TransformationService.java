package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.Transformation;
import io.cptn.common.repositories.TransformationRepository;
import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/16/23 */
@Service
@RequiredArgsConstructor
public class TransformationService extends CommonService {
    private final TransformationRepository transformationRepository;

    public Transformation create(Transformation transformation) {
        return save(transformation);
    }

    @CacheEvict(value = "transformation-proc", key = "#transformation.id")
    public Transformation update(Transformation transformation) {
        return save(transformation);
    }

    @CacheEvict(value = "transformation-proc", key = "#transformation.id")
    public void delete(Transformation transformation) {
        transformationRepository.delete(transformation);
    }

    private Transformation save(Transformation transformation) {
        return transformationRepository.save(transformation);
    }

    public List<Transformation> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return transformationRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public long count() {
        return transformationRepository.count();
    }


    public Optional<Transformation> getById(UUID id) {
        return transformationRepository.findById(id);
    }
}
