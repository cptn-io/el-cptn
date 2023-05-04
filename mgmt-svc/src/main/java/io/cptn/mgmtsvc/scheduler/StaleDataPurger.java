package io.cptn.mgmtsvc.scheduler;

import io.cptn.common.repositories.PipelineTriggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/* @author: kc, created on 5/3/23 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StaleDataPurger {

    private final PipelineTriggerRepository pipelineTriggerRepository;

    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(name = "stalePurger", lockAtLeastFor = "5m", lockAtMostFor = "10m")
    public void run() {
        try {
            log.info("Running Stale Data Purger");
            pipelineTriggerRepository.deleteStaleData();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
