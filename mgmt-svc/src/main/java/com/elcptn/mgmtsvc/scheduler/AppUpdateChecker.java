package com.elcptn.mgmtsvc.scheduler;

import com.elcptn.common.entities.Settings;
import com.elcptn.mgmtsvc.services.AppSynchronizer;
import com.elcptn.mgmtsvc.services.SettingsService;
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

    private static final String APP_CHECK_INTERVAL = "app.check.interval";

    private static final String NEXT_CHECK_TIMESTAMP = "next.app.check.timestamp";

    private static final Long DEFAULT_CHECK_INTERVAL = 6 * 60 * 60 * 1000L;

    private final AppSynchronizer appSynchronizer;

    private final SettingsService settingsService;

    @Scheduled(cron = "0 */15 * * * *")
    @SchedulerLock(name = "appChecker", lockAtLeastFor = "5m", lockAtMostFor = "10m")
    public void run() {
        try {
            log.info("Running App Update Checker");

            Long interval = getAppCheckInterval();

            Settings nextCheckAt = settingsService.get(APP_CHECK_INTERVAL);
            if (nextCheckAt == null) {
                //check was never run before, run now
                runAppUpdateCheck();
                setNextRotationTimestamp(System.currentTimeMillis() + interval);
                return;
            }


            Long nextCheckAtLong = parseNextCheckTimestamp(nextCheckAt.getValue());
            if (nextCheckAtLong == null) {
                setNextRotationTimestamp(System.currentTimeMillis() + interval);
                return;
            }

            if (nextCheckAtLong > System.currentTimeMillis()) {
                // not yet time to check
                return;
            }

            runAppUpdateCheck();
            setNextRotationTimestamp(System.currentTimeMillis() + interval);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }


        try {
            log.info("Running App Update Checker");
            appSynchronizer.syncWithRepository();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private Long parseNextCheckTimestamp(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid next app check timestamp: " + value);
            return null;
        }
    }

    private void setNextRotationTimestamp(Long timestamp) {
        settingsService.upsert(NEXT_CHECK_TIMESTAMP, String.valueOf(timestamp), true);
    }

    private Long getAppCheckInterval() {
        Long interval = DEFAULT_CHECK_INTERVAL;
        Settings checkInterval = settingsService.get(APP_CHECK_INTERVAL);
        if (checkInterval != null) {
            String intervalString = checkInterval.getValue();
            try {
                interval = Long.parseLong(intervalString);
            } catch (Exception e) {
                log.warn("Invalid app check interval: " + intervalString + ", using default: " + interval);
            }
        }
        return interval;
    }

    private void runAppUpdateCheck() {
        log.info("Running App Update Checker");
        appSynchronizer.syncWithRepository();
    }
}
