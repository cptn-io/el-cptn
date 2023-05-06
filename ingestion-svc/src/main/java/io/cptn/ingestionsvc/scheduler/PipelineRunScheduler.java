package io.cptn.ingestionsvc.scheduler;

import io.cptn.common.entities.PipelineTrigger;
import io.cptn.common.entities.State;
import io.cptn.common.repositories.PipelineScheduleRepository;
import io.cptn.common.repositories.PipelineTriggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
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
    @SchedulerLock(name = "pipelineRunScheduler", lockAtLeastFor = "30s", lockAtMostFor = "3m")
    public void run() {
        try {
            LockAssert.assertLocked();
            if (log.isDebugEnabled()) {
                log.debug("Running Pipeline Run Scheduler");
            }
            pipelineScheduleRepository.findPipelineScheduleByActiveTrueAndNextRunAtLessThanEqual(ZonedDateTime.now()).forEach(pipelineSchedule -> {
                try {
                    pipelineSchedule.computeNextRunAt();
                    pipelineScheduleRepository.save(pipelineSchedule);

                    if (!pipelineSchedule.getPipeline().getBatchProcess()) {
                        log.debug("Skipping pipeline {} as it is not a batch process", pipelineSchedule.getPipeline().getName());
                        return;
                    }

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
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
