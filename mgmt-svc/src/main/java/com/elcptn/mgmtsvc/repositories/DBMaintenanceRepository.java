package com.elcptn.mgmtsvc.repositories;

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
        String query = "TRUNCATE TABLE outbound_rotated_queue;" +
                "BEGIN;" +
                "ALTER TABLE outbound_write_queue RENAME TO outbound_write_queue_temp;" +
                "ALTER TABLE outbound_rotated_queue RENAME TO outbound_write_queue;" +
                "ALTER TABLE outbound_write_queue_temp RENAME TO outbound_rotated_queue;" +
                "COMMIT;";
        entityManager.createNativeQuery(query).executeUpdate();
    }
}
