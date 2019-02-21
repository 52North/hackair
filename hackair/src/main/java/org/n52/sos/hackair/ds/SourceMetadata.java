package org.n52.sos.hackair.ds;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.util.DateTimeHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SourceMetadata implements Serializable {
    private static final long serialVersionUID = 4925075934559259868L;
    
    private static final Integer MULTIPLIER =2;
    private static final String DEFAULT_PERIOD = "P30D";

    private String source;

    private String lastDateTime;
    
    private String interval;

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     * @return 
     */
    public SourceMetadata setSource(String source) {
        this.source = source;
        return this;
    }

    /**
     * @return the lastDateTime
     */
    public String getLastDateTime() {
        return lastDateTime;
    }

    /**
     * @param lastDateTime the lastDateTime to set
     * @return 
     */
    public SourceMetadata setLastDateTime(String lastDateTime) {
        this.lastDateTime = lastDateTime;
        return this;
    }

    public boolean hasLastDateTime() {
        return getLastDateTime() != null && !getLastDateTime().isEmpty();
    }
    
    /**
     * @param lastDateTime the lastDateTime to set
     * @return 
     */
    public SourceMetadata setLastDateTime(DateTime lastDateTime) {
        return setLastDateTime(DateTimeHelper.formatDateTime2IsoString(lastDateTime));
    }
    
    /**
     * @return the lastDateTime
     * @throws DateTimeParseException 
     */
    @JsonIgnore
    public DateTime getLastDateTimeAsDateTime() throws DateTimeParseException {
        return hasLastDateTime() ? DateTimeHelper.parseIsoString2DateTime(getLastDateTime()) : null;
    }

    /**
     * @return the interval
     */
    public String getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     * @return 
     */
    public SourceMetadata setInterval(String interval) {
        this.interval = interval;
        return this;
    }
    
    /**
     * @param period
     * @return 
     */
    public SourceMetadata setInterval(Period period) {
        setInterval(period.toString());
        return this;
    }

    /**
     * @return
     */
    public boolean hasInterval() {
        return getInterval() != null && !getInterval().isEmpty();
    }
    
    /**
     * @return
     */
    @JsonIgnore
    public Period getIntervalAsPeriod() {
        return hasInterval() ? Period.parse(getInterval()) : Period.parse(DEFAULT_PERIOD);
    }

    /**
     * @return 
     * 
     */
    @JsonIgnore
    public Period reduceInterval() {
        setInterval(getIntervalAsPeriod().toStandardDuration().dividedBy(MULTIPLIER).toPeriod());
        return getIntervalAsPeriod();
    }

    @JsonIgnore
    public Period increaseInterval() {
        setInterval(getIntervalAsPeriod().toStandardDuration().multipliedBy(MULTIPLIER).toPeriod());
        return getIntervalAsPeriod();
    }
}