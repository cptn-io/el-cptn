package com.elcptn.mgmtsvc.scheduler;

import com.elcptn.mgmtsvc.entities.Event;
import com.elcptn.mgmtsvc.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* @author: kc, created on 2/22/23 */
@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InboundEventProcessor {

    private final EventRepository eventRepository;

    @Scheduled(fixedDelay = 5000)
    public void process() {

        List<Event> eventList = eventRepository.fetchEventsForProcessing(10);
        System.out.println(eventList.size());

    }

}
