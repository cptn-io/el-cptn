package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.Extractor;
import io.cptn.common.repositories.ExtractorRepository;
import io.cptn.common.services.CommonService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 6/14/23 */
@Service
@RequiredArgsConstructor
public class ExtractorService extends CommonService {

    private final ExtractorRepository extractorRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    public Extractor create(Extractor extractor) {
        return save(extractor);
    }

    public Extractor update(Extractor extractor, boolean forceUpdate) {
        if (forceUpdate) {
            //force update if there are updates to config. JPA doesn't detect object changes within a list
            Session session = (Session) entityManager.getDelegate();
            session.evict(extractor);
        }
        return save(extractor);
    }

    public Optional<Extractor> getBySource(UUID sourceId) {
        return extractorRepository.findBySourceId(sourceId);
    }

    public Optional<Extractor> getById(UUID id) {
        return extractorRepository.findById(id);
    }

    public void delete(Extractor extractor) {
        extractorRepository.delete(extractor);
    }

    private Extractor save(Extractor extractor) {
        return extractorRepository.save(extractor);
    }
}
