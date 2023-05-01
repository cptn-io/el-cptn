package com.elcptn.mgmtsvc.scheduler;

import com.elcptn.common.entities.Settings;
import com.elcptn.common.repositories.DBMaintenanceRepository;
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
public class TableRotator {

    private static final String TABLE_ROTATION_INTERVAL = "table.rotation.interval";

    private static final String NEXT_ROTATION_TIMESTAMP = "next.rotation.timestamp";

    private static final Long DEFAULT_ROTATION_INTERVAL = 24 * 60 * 60 * 1000L;

    private final DBMaintenanceRepository dbMaintenanceRepository;

    private final SettingsService settingsService;

    @Scheduled(cron = "0 */15 * * * *")
    @SchedulerLock(name = "tableRotator", lockAtLeastFor = "5m", lockAtMostFor = "10m")
    public void run() {
        try {
            log.info("Running Table Rotator");

            Long interval = getRotationInterval();

            Settings nextRotationAt = settingsService.get(NEXT_ROTATION_TIMESTAMP);
            if (nextRotationAt == null) {
                //tables have never been rotated
                setNextRotationTimestamp(System.currentTimeMillis() + interval);
                return;
            }


            Long nextRotationAtLong = parseNextRotationTimestamp(nextRotationAt.getValue());
            if (nextRotationAtLong == null) {
                setNextRotationTimestamp(System.currentTimeMillis() + interval);
                return;
            }

            if (nextRotationAtLong > System.currentTimeMillis()) {
                // not yet time to rotate
                return;
            }

            rotateTables();
            setNextRotationTimestamp(System.currentTimeMillis() + interval);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Long parseNextRotationTimestamp(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid next rotation timestamp: " + value);
            return null;
        }
    }

    private void setNextRotationTimestamp(Long timestamp) {
        settingsService.upsert(NEXT_ROTATION_TIMESTAMP, String.valueOf(timestamp), true);
    }

    private Long getRotationInterval() {
        Long interval = DEFAULT_ROTATION_INTERVAL;
        Settings rotationInterval = settingsService.get(TABLE_ROTATION_INTERVAL);
        if (rotationInterval == null) {
            String intervalString = rotationInterval.getValue();
            try {
                interval = Long.parseLong(intervalString);
            } catch (Exception e) {
                log.warn("Invalid rotation interval: " + intervalString + ", using default: " + interval);
            }
        }
        return interval;
    }

    private void rotateTables() {
        log.info("Rotating inbound and outbound tables");
        dbMaintenanceRepository.rotateOutboundQueues();
        dbMaintenanceRepository.rotateInboundQueues();
    }

}
