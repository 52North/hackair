package org.n52.sos.hackair.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.n52.sos.exception.CodedException;
import org.n52.sos.hackair.ds.HackAIRConfiguration;

public class HelperTest implements HackAIRHelper {
    
    @Test
    public void test_readConfig() throws CodedException {
        HackAIRConfiguration config = readConfig(null);
        assertNotNull(config);
    }
    
    @Test
    public void test_writeConfig() throws CodedException {
        HackAIRConfiguration config = readConfig(null);
        writeConfig(config);
    }

}
