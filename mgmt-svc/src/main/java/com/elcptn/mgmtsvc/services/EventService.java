package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Event;
import com.elcptn.mgmtsvc.repositories.EventRepository;
import org.springframework.stereotype.Service;

/* @author: kc, created on 2/8/23 */
@Service
public class EventService extends CommonService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void create(Event event) {
        eventRepository.save(event);
    }
}
