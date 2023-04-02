package com.elcptn.mgmtsvc.scheduler;

import com.elcptn.common.entities.InboundEvent;
import com.elcptn.mgmtsvc.repositories.InboundEventRepository;
import com.elcptn.mgmtsvc.scheduler.processors.InboundEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

/* @author: kc, created on 2/22/23 */
@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InboundEventScheduler {

    private final InboundEventRepository eventRepository;
    private final InboundEventProcessor inboundEventProcessor;

    private final ForkJoinPool forkJoinPool;

    @Scheduled(fixedDelayString = "${inbound.event.processor.interval:5000}")
    public void run() {
        log.debug("Running inbound event processor");
        processRecords();
    }

    @Transactional
    public void processRecords() {
        try (Stream<InboundEvent> eventStream = eventRepository.fetchEventsForProcessing()) {
            eventStream.forEach(event -> {
                forkJoinPool.submit(() -> {
                    inboundEventProcessor.processEvent(event);
                });
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
