package org.n52.sos.hackair.ds;

import static org.junit.Assert.assertTrue;

import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.n52.sos.exception.CodedException;
import org.n52.sos.hackair.util.HackAIRHelper;

public class HackAIRConfigurationTest implements HackAIRHelper {

    private HackAIRConfiguration config;
    
    @Before
    public void setUp() throws CodedException {
        this.config = readConfig("/hackair.json");
    }
    
    @Test
    public void test_reduce_period() {
        for (SourceMetadata source : config.getSources()) {
            assertTrue(source.hasInterval());
            Period period = source.getIntervalAsPeriod();
            source.reduceInterval();
            assertTrue(source.getIntervalAsPeriod().equals(calculatePeriodReduce(period)));
            
        }
    }
    
    @Test
    public void test_increase_period() {
        for (SourceMetadata source : config.getSources()) {
            assertTrue(source.hasInterval());
            Period period = source.getIntervalAsPeriod();
            source.increaseInterval();
            assertTrue(source.getIntervalAsPeriod().equals(calculatePeriodIncrease(period)));
            
        }
    }

    private Period calculatePeriodReduce(Period period) {
        return period.toStandardDuration().dividedBy(2).toPeriod();
    }
    
    private Period calculatePeriodIncrease(Period period) {
        return period.toStandardDuration().multipliedBy(2).toPeriod();
    }
    
}
