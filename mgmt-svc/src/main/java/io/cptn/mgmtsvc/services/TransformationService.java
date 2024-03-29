package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.App;
import io.cptn.common.entities.AppType;
import io.cptn.common.entities.Transformation;
import io.cptn.common.repositories.TransformationRepository;
import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 2/16/23 */
@Service
@RequiredArgsConstructor
public class TransformationService extends CommonService {
    private final TransformationRepository transformationRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    public Transformation create(Transformation transformation) {
        return save(transformation);
    }

    @CacheEvict(value = "transformation-proc", key = "#transformation.id")
    public Transformation update(Transformation transformation, boolean forceUpdate) {
        if (forceUpdate) {
            //force update if there are updates to config. JPA doesn't detect object changes within a list
            Session session = (Session) entityManager.getDelegate();
            session.evict(transformation);
        }
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
        return transformationRepository.findAll(pageable).stream().toList();
    }

    public long count() {
        return transformationRepository.count();
    }


    public Optional<Transformation> getById(UUID id) {
        return transformationRepository.findById(id);
    }

    public App exportAsApp(Transformation transformation) {
        String key = hash(transformation.getId().toString());
        App app = new App();
        app.setName(transformation.getName());
        app.setConfig(List.of());
        app.setLogoUrl(null);
        app.setKey(key);
        app.setConfig(getConfig(transformation));
        app.setHash(hash(key + Instant.now().toString()));
        app.setScript(transformation.getScript());
        app.setType(AppType.TRANSFORMATION);
        return app;
    }
}
