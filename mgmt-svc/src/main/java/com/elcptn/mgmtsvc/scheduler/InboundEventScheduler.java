package com.elcptn.mgmtsvc.scheduler;

import com.elcptn.mgmtsvc.entities.Event;
import com.elcptn.mgmtsvc.repositories.EventRepository;
import com.elcptn.mgmtsvc.scheduler.processors.InboundEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

/* @author: kc, created on 2/22/23 */
@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InboundEventScheduler {

    private final EventRepository eventRepository;
    private final InboundEventProcessor inboundEventProcessor;
    private final ForkJoinPool forkJoinPool;

    @Value("${inbound.event.processor.batch-size:20}")
    private Integer batchSize;

    @Scheduled(fixedDelayString = "${inbound.event.processor.interval:5000}")
    public void run() {

        log.debug("Running inbound event processor");
        while (true) {
            //keep going as long as there are outstanding records to be fetched
            int count = processRecords();
            if (count == 0) {
                break;
            }
        }
    }

    private int processRecords() {
        List<Event> eventList = eventRepository.fetchEventsForProcessing(batchSize);
        if (eventList.size() == 0) {
            return 0;
        }

        try {
            forkJoinPool.submit(() -> eventList.stream().parallel()
                    .forEach(event ->
                            inboundEventProcessor.processEvent(event)
                    )
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return eventList.size();
    }

}
