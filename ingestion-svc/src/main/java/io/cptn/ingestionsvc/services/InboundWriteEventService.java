package io.cptn.ingestionsvc.services;

import io.cptn.common.entities.InboundWriteEvent;
import io.cptn.common.repositories.InboundWriteEventRepository;
import io.cptn.common.services.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/* @author: kc, created on 2/8/23 */
@Service
@RequiredArgsConstructor
public class InboundWriteEventService extends CommonService {

    private final InboundWriteEventRepository writeEventRepository;

    public InboundWriteEvent create(InboundWriteEvent event) {
        return writeEventRepository.save(event);
    }

}
