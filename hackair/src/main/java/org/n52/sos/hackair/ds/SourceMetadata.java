package org.n52.sos.hackair.ds;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.util.DateTimeHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SourceMetadata implements Serializable {
    private static final long serialVersionUID = 4925075934559259868L;

    public String source;

    public String lastDateTime;

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
}