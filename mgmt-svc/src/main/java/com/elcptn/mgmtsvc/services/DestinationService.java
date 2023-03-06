package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Destination;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/16/23 */
@Service
@RequiredArgsConstructor
public class DestinationService extends CommonService {
    private final DestinationRepository destinationRepository;

    private final SourceService sourceService;

    public Destination create(Destination destination) {
        return save(destination);
    }

    public Destination update(Destination destination) {
        return save(destination);
    }

    public void delete(Destination destination) {
        destinationRepository.delete(destination);
    }

    private Destination save(Destination destination) {
        return destinationRepository.save(destination);
    }

    public List<Destination> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return destinationRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public long count() {
        return destinationRepository.count();
    }


    public Optional<Destination> getById(UUID id) {
        return destinationRepository.findById(id);
    }
}
