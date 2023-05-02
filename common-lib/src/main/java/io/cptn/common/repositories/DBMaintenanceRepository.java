package io.cptn.common.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/* @author: kc, created on 3/9/23 */
@Repository
@RequiredArgsConstructor
public class DBMaintenanceRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void rotateOutboundQueues() {
        String query = "TRUNCATE TABLE outbound_rotated_2;" +
                "BEGIN;" +
                "ALTER TABLE outbound_write_queue RENAME TO outbound_write_queue_temp;" +
                "ALTER TABLE outbound_rotated_1 RENAME TO outbound_write_1_temp;" +
                "ALTER TABLE outbound_rotated_2 RENAME TO outbound_write_queue;" +
                "ALTER TABLE outbound_write_queue_temp RENAME TO outbound_rotated_1;" +
                "ALTER TABLE outbound_write_1_temp RENAME TO outbound_rotated_2;" +
                "COMMIT;";
        entityManager.createNativeQuery(query).executeUpdate();
    }

    @Transactional
    public void rotateInboundQueues() {
        String query = "TRUNCATE TABLE inbound_rotated_2;" +
                "BEGIN;" +
                "ALTER TABLE inbound_write_queue RENAME TO inbound_write_queue_temp;" +
                "ALTER TABLE inbound_rotated_1 RENAME TO inbound_write_1_temp;" +
                "ALTER TABLE inbound_rotated_2 RENAME TO inbound_write_queue;" +
                "ALTER TABLE inbound_write_queue_temp RENAME TO inbound_rotated_1;" +
                "ALTER TABLE inbound_write_1_temp RENAME TO inbound_rotated_2;" +
                "COMMIT;";
        entityManager.createNativeQuery(query).executeUpdate();
    }
}


