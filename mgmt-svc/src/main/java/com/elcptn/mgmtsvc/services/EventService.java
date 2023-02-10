package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Event;
import com.elcptn.mgmtsvc.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/* @author: kc, created on 2/8/23 */
@Service
@RequiredArgsConstructor
public class EventService extends CommonService {

    private final EventRepository eventRepository;

    public void create(Event event) {
        eventRepository.save(event);
    }
}
