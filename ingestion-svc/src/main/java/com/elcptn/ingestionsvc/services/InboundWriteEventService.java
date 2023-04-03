package com.elcptn.ingestionsvc.services;

import com.elcptn.common.entities.InboundWriteEvent;
import com.elcptn.common.repositories.InboundWriteEventRepository;
import com.elcptn.common.services.CommonService;
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
