package org.n52.sos.hackair.ds;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.util.DateTimeHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class HackAIRConfiguration implements Serializable {
    private static final long serialVersionUID = 198734196404914182L;

    public String url;

    public String accept;
    
    private String globalStartTime;
    
    private String cronExpression;
    
    public Set<SourceMetadata> sources = new LinkedHashSet<>();
    
    public HackAIRConfiguration() {
        setUrl("https://api.hackair.eu/");
        setAccept("application/vnd.hackair.v1+json");
        setGlobalStartTime( DateTime.now().toString());
        setCronExpression("0 0/30 * * * ?");
        addSource(new SourceMetadata().setSource("webservices"));
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     * @return 
     */
    public HackAIRConfiguration setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * @return the accept
     */
    public String getAccept() {
        return accept;
    }

    /**
     * @param accept
     *            the accept to set
     * @return 
     */
    public HackAIRConfiguration setAccept(String accept) {
        this.accept = accept;
        return this;
    }

    /**
     * @return the globalStartTime
     */
    public String getGlobalStartTime() {
        return globalStartTime;
    }
    
    /**
     * @return the globalStartTime
     * @throws DateTimeParseException 
     */
    @JsonIgnore
    public DateTime getGlobalStartTimeAsDateTime() throws DateTimeParseException {
        return DateTimeHelper.parseIsoString2DateTime(getGlobalStartTime());
    }

    /**
     * @param globalStartTime the globalStartTime to set
     */
    public void setGlobalStartTime(String globalStartTime) {
        this.globalStartTime = globalStartTime;
    }

    /**
     * @return the cronExpression
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * @param cronExpression the cronExpression to set
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * @return the sources
     */
    public Set<SourceMetadata> getSources() {
        return sources;
    }

    /**
     * @param sources
     *            the sources to set
     * @return 
     */
    public HackAIRConfiguration setSources(Set<SourceMetadata> sources) {
        this.sources.clear();
        this.sources = sources;
        return this;
    }
    
    /**
     * @param sources
     *            the sources to set
     * @return 
     */
    public HackAIRConfiguration addSources(Set<SourceMetadata> sources) {
        this.sources.addAll(sources);
        return this;
    }
    
    /**
     * @param sources
     *            the sources to set
     * @return 
     */
    public HackAIRConfiguration addSource(SourceMetadata source) {
        this.sources.add(source);
        return this;
    }
}