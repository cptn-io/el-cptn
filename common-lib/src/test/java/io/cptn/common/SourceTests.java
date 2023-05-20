package io.cptn.common;

import io.cptn.common.entities.Source;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/* @author: kc, created on 2/7/23 */
class SourceTests {
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
        assertEquals(prevPrimary, source.getSecondaryKey());
        assertNotEquals(prevPrimary, source.getPrimaryKey());
    }

    @Test
    void checkKeysTest() {
        Source source = new Source();
        assertFalse(source.hasAnyKeysSetup());
        source.setupNewKeys();
        assertTrue(source.hasAnyKeysSetup());
    }

    @Test
    void checkDefaultValuesTest() {
        Source source = new Source();
        assertTrue(source.getSecured());
        assertTrue(source.getActive());
        assertNull(source.getPrimaryKey());
        assertNull(source.getSecondaryKey());
        assertNull(source.getLastKeyRotationAt());
    }

}
