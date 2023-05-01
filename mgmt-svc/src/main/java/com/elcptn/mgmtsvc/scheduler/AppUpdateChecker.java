package com.elcptn.mgmtsvc.scheduler;

import com.elcptn.mgmtsvc.services.AppSynchronizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/* @author: kc, created on 5/1/23 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AppUpdateChecker {

    private final AppSynchronizer appSynchronizer;

    @Scheduled(fixedDelay = 6 * 60 * 60 * 1000)
    @SchedulerLock(name = "appChecker", lockAtLeastFor = "5m", lockAtMostFor = "5m")
    public void run() {
        try {
            log.info("Running App Update Checker");
            appSynchronizer.syncWithRepository();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
