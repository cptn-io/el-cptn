package com.elcptn.mgmtsvc;

import com.elcptn.mgmtsvc.entities.Workflow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/* @author: kc, created on 2/7/23 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkflowTests {

    @Test
    void setupKeysTest() {
        Workflow workflow = new Workflow();
        workflow.setSecured(true);
        workflow.setupNewKeys();
        assertNotNull(workflow.getPrimaryKey());
        assertNotNull(workflow.getSecondaryKey());
        assertNotNull(workflow.getLastKeyRotationAt());
    }

    @Test
    void rotateKeysTest() {
        Workflow workflow = new Workflow();
        workflow.setSecured(true);
        workflow.setupNewKeys();
        String prevPrimary = workflow.getPrimaryKey();

        workflow.rotateKeys();
        assertNotNull(workflow.getPrimaryKey());
        assertEquals(prevPrimary, workflow.getSecondaryKey());
        assertNotEquals(prevPrimary, workflow.getPrimaryKey());
    }

    @Test
    void checkKeysTest() {
        Workflow workflow = new Workflow();
        assertFalse(workflow.hasAnyKeysSetup());
        workflow.setupNewKeys();
        assertTrue(workflow.hasAnyKeysSetup());
    }
}
