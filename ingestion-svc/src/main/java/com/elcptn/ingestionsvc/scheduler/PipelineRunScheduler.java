package com.elcptn.ingestionsvc.scheduler;

import com.elcptn.common.entities.PipelineTrigger;
import com.elcptn.common.entities.State;
import com.elcptn.common.repositories.PipelineScheduleRepository;
import com.elcptn.common.repositories.PipelineTriggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/* @author: kc, created on 4/5/23 */
@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PipelineRunScheduler {

    private final PipelineScheduleRepository pipelineScheduleRepository;

    private final PipelineTriggerRepository pipelineTriggerRepository;

    @Scheduled(fixedDelay = 60000)
    public void run() {
        log.info("Running Pipeline Run Scheduler");
        pipelineScheduleRepository.findPipelineScheduleByActiveTrueAndNextRunAtLessThanEqual(ZonedDateTime.now()).forEach(pipelineSchedule -> {
            pipelineSchedule.computeNextRunAt();
            pipelineScheduleRepository.save(pipelineSchedule);

            Long queuedTriggers =
                    pipelineTriggerRepository.countByPipelineIdAndStateEquals(pipelineSchedule.getPipeline().getId(),
                            State.QUEUED);
            if (queuedTriggers > 0) {
                log.debug("Skipping pipeline {} as there are {} queued triggers", pipelineSchedule.getPipeline().getName(),
                        queuedTriggers);
                return;
            }
            PipelineTrigger pipelineTrigger = new PipelineTrigger();
            pipelineTrigger.setPipeline(pipelineSchedule.getPipeline());
            pipelineTriggerRepository.save(pipelineTrigger);
        });
    }
}
