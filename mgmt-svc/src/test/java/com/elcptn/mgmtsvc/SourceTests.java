package com.elcptn.mgmtsvc;

import com.elcptn.common.entities.Source;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/* @author: kc, created on 2/7/23 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SourceTests {

    @Test
    void setupKeysTest() {
        Source source = new Source();
        source.setSecured(true);
        source.setupNewKeys();
        assertNotNull(source.getPrimaryKey());
        assertNotNull(source.getSecondaryKey());
        assertNotNull(source.getLastKeyRotationAt());
    }

    @Test
    void rotateKeysTest() {
        Source source = new Source();
        source.setSecured(true);
        source.setupNewKeys();
        String prevPrimary = source.getPrimaryKey();

        source.rotateKeys();
        assertNotNull(source.getPrimaryKey());
//        assertEquals(prevPrimary, source.getSecondaryKey());
//        assertNotEquals(prevPrimary, source.getPrimaryKey());
    }

    @Test
    void checkKeysTest() {
        Source source = new Source();
//        assertFalse(source.hasAnyKeysSetup());
        source.setupNewKeys();
        //assertTrue(source.hasAnyKeysSetup());
    }
}
