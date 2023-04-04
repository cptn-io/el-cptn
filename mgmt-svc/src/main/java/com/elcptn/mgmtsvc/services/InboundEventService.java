package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.InboundEvent;
import com.elcptn.common.repositories.InboundEventRepository;
import com.elcptn.common.web.ListEntitiesParam;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 4/3/23 */
@Service
public class InboundEventService extends CommonService {

    private final InboundEventRepository inboundEventRepository;

    public InboundEventService(InboundEventRepository inboundEventRepository) {
        this.inboundEventRepository = inboundEventRepository;
    }

    public List<InboundEvent> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return inboundEventRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public Optional<InboundEvent> getById(UUID id) {
        return inboundEventRepository.findById(id);
    }

    public long count() {
        return inboundEventRepository.count();
    }

}
