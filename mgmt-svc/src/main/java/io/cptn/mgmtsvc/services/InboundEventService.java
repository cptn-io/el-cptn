package io.cptn.mgmtsvc.services;

import com.querydsl.core.types.Predicate;
import io.cptn.common.entities.InboundEvent;
import io.cptn.common.entities.InboundWriteEvent;
import io.cptn.common.repositories.InboundEventRepository;
import io.cptn.common.repositories.InboundWriteEventRepository;
import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 4/3/23 */
@Service
@RequiredArgsConstructor
public class InboundEventService extends CommonService {

    private final InboundEventRepository inboundEventRepository;

    private final InboundWriteEventRepository inboundWriteEventRepository;
    
    public List<InboundEvent> getAll(ListEntitiesParam param, Predicate predicate) {
        Pageable pageable = getPageable(param);
        if (predicate == null) {
            return inboundEventRepository.findAll(pageable).stream().toList();
        } else {
            return inboundEventRepository.findAll(predicate, pageable).stream().toList();
        }
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

    public InboundWriteEvent resendEvent(InboundEvent inboundEvent) {
        InboundWriteEvent event = new InboundWriteEvent();
        event.setSource(inboundEvent.getSource());
        event.setPayload(inboundEvent.getPayload());
        return inboundWriteEventRepository.save(event);
    }
}
