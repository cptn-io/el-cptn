package io.cptn.mgmtsvc.scheduler;

import io.cptn.common.entities.Settings;
import io.cptn.common.repositories.InboundEventRepository;
import io.cptn.common.repositories.OutboundEventRepository;
import io.cptn.mgmtsvc.services.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/* @author: kc, created on 5/5/23 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TableCleaner {

    private static final String DATA_STORAGE_INTERVAL_KEY = "data.storage.interval";

    private static final Long DEFAULT_DATA_STORAGE_INTERVAL = 3 * 24 * 60 * 60 * 1000L;

    private final SettingsService settingsService;

    private final InboundEventRepository inboundEventRepository;

    private final OutboundEventRepository outboundEventRepository;

    @Scheduled(cron = "0 */15 * * * *")
    @SchedulerLock(name = "tableCleaner", lockAtLeastFor = "5m", lockAtMostFor = "10m")
    public void run() {
        try {
            Long storageInterval = getStorageInterval();
            ZonedDateTime createdBefore = ZonedDateTime.now().minus(storageInterval, ChronoUnit.MILLIS);
            log.info("Running Table Cleaner to purge data older than: " + createdBefore + ", interval(ms): " + storageInterval);
            inboundEventRepository.purgeStaleDataInInboundQueue(createdBefore);
            outboundEventRepository.purgeStaleDataInOutboundQueue(createdBefore);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Long getStorageInterval() {
        Long interval = DEFAULT_DATA_STORAGE_INTERVAL;
        Settings storageInterval = settingsService.get(DATA_STORAGE_INTERVAL_KEY);
        if (storageInterval != null) {
            String intervalString = storageInterval.getValue();
            try {
                interval = Long.parseLong(intervalString);
            } catch (Exception e) {
                log.warn("Invalid data storage interval: " + intervalString + ", using default: " + interval);
            }
        }
        return interval;
    }
}
