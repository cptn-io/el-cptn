package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.InboundEvent;
import com.elcptn.common.repositories.InboundEventRepository;
import com.elcptn.common.services.CommonService;
import com.elcptn.common.web.ListEntitiesParam;
import com.querydsl.core.types.Predicate;
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

    public List<InboundEvent> getAll(ListEntitiesParam param, Predicate predicate) {
        Pageable pageable = getPageable(param);
        return inboundEventRepository.findAll(predicate, pageable).stream().collect(Collectors.toList());
    }

    public Optional<InboundEvent> getById(UUID id) {
        return inboundEventRepository.findById(id);
    }

    public long count() {
        return count(null);
    }

    public long count(Predicate predicate) {
        if (predicate != null) {
            return inboundEventRepository.count(predicate);
        }

        return inboundEventRepository.count();
    }

    public InboundEvent resendEvent(InboundEvent inboundEvent) {
        InboundEvent event = new InboundEvent();
        event.setSource(inboundEvent.getSource());
        event.setPayload(inboundEvent.getPayload());
        return inboundEventRepository.save(event);
    }
}
