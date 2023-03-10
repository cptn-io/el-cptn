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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/* @author: kc, created on 2/22/23 */
@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InboundEventScheduler {

    private final EventRepository eventRepository;

    private final InboundEventProcessor inboundEventProcessor;
    @Value("${inbound.event.processor.batch-size:20}")
    private Integer batchSize;

    @Scheduled(fixedDelayString = "${inbound.event.processor.interval:1000}")
    public void run() {
        log.debug("Running inbound event processor");
        long startTime = ZonedDateTime.now().toEpochSecond();
        while (processRecords() > 0) {
        }
        long endTime = ZonedDateTime.now().toEpochSecond();
        log.error("Time to process=" + (endTime - startTime));
    }


    private int processRecords() {
        List<Event> eventList = eventRepository.fetchEventsForProcessing(batchSize);
        try {
            eventList.forEach(event -> {
                log.error("processing event=" + event.getId());

                new RecursiveAction() {
                    @Override
                    protected void compute() {
                        inboundEventProcessor.processEvent(event);
                    }
                }.fork();
            });

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

//        eventList.forEach(event -> {
//            try {
//                Thread t = new Thread(() -> forkJoinPool.submit(() -> {
//                    inboundEventProcessor.processEvent(event);
//                }).invoke());
//                t.start();
//                t.join();
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//            }
//        });

        return eventList.size();
    }

}
