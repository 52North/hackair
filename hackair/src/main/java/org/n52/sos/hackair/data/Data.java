
package org.n52.sos.hackair.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Data {

    private Long datetime;
    private Loc loc;
    @JsonProperty("source_type")
    private String sourceType;
    @JsonProperty("pollutant_q")
    private PollutantQ pollutantQ;
    @JsonProperty("pollutant_i")
    private PollutantI pollutantI;
    @JsonProperty("date_str")
    private String dateStr;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("source_info")
    private SourceInfo sourceInfo;
    @JsonProperty("_id")
    private String id;
    @JsonProperty("real_datetime")
    private String realDatetime;
    private String city;
    private String photo;

    @JsonProperty("datetime")
    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public Loc getLoc() {
        return loc;
    }

    public void setLoc(Loc loc) {
        this.loc = loc;
    }

    @JsonProperty("source_type")
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("source_type")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @JsonProperty("pollutant_q")
    public PollutantQ getPollutantQ() {
        return pollutantQ;
    }

    @JsonProperty("pollutant_q")
    public void setPollutantQ(PollutantQ pollutantQ) {
        this.pollutantQ = pollutantQ;
    }

    @JsonProperty("pollutant_i")
    public PollutantI getPollutantI() {
        return pollutantI;
    }

    @JsonProperty("pollutant_i")
    public void setPollutantI(PollutantI pollutantI) {
        this.pollutantI = pollutantI;
    }

    @JsonProperty("date_str")
    public String getDateStr() {
        return dateStr;
    }

    @JsonProperty("date_str")
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("source_info")
    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    @JsonProperty("source_info")
    public void setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }
    
    public boolean hasSourceInfo() {
        return getSourceInfo() != null;
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("real_datetime")
    public String getRealDatetime() {
        return realDatetime;
    }

    @JsonProperty("real_datetime")
    public void setRealDatetime(String realDatetime) {
        this.realDatetime = realDatetime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


}
