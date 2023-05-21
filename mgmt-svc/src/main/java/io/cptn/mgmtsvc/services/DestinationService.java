package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.App;
import io.cptn.common.entities.AppType;
import io.cptn.common.entities.Destination;
import io.cptn.common.pojos.ConfigItem;
import io.cptn.common.projections.DestinationView;
import io.cptn.common.repositories.DestinationRepository;
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
public class DestinationService extends CommonService {

    private final DestinationRepository destinationRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    public Destination create(Destination destination) {
        return save(destination);
    }

    @CacheEvict(value = "destination-proc", key = "#destination.id")
    public Destination update(Destination destination, boolean forceUpdate) {
        if (forceUpdate) {
            //force update if there are updates to config. JPA doesn't detect object changes within a list
            Session session = (Session) entityManager.getDelegate();
            session.evict(destination);
        }
        return save(destination);
    }

    @CacheEvict(value = "destination-proc", key = "#destination.id")
    public void delete(Destination destination) {
        destinationRepository.delete(destination);
    }

    private Destination save(Destination destination) {
        return destinationRepository.save(destination);
    }

    public List<DestinationView> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return destinationRepository.findAllProjectedBy(pageable).stream().toList();
    }

    public long count() {
        return destinationRepository.count();
    }


    public Optional<Destination> getById(UUID id) {
        return destinationRepository.findById(id);
    }

    public App exportAsApp(Destination destination) {

        String key = hash(destination.getId().toString());
        App app = new App();
        app.setName(destination.getName());

        List<ConfigItem> configItems = destination.getConfig();
        if (configItems != null) {
            configItems = configItems.stream().map(configItem -> {
                configItem.setValue("");
                return configItem;
            }).toList();
        } else {
            configItems = List.of();
        }

        app.setConfig(configItems);
        app.setLogoUrl(null);
        app.setKey(key);
        app.setHash(hash(key + Instant.now().toString()));
        app.setScript(destination.getScript());
        app.setType(AppType.DESTINATION);
        return app;
    }
}
