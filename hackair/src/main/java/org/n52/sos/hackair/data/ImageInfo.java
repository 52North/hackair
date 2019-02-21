
package org.n52.sos.hackair.data;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageInfo {

    private String path;
    private Concepts concepts;
    private Boolean containsSky;
    private Double rg;
    private Object id;
    @JsonProperty("all_pixels")
    private Integer allPixels;
    private Double gb;
    private Boolean usableSky;
    @JsonProperty("sky_pixels")
    private Integer skyPixels;
    @JsonProperty("rg_ratio")
    private Double rgRatio;
    @JsonProperty("gb_ratio")
    private Double gbRatio;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Concepts getConcepts() {
        return concepts;
    }

    public void setConcepts(Concepts concepts) {
        this.concepts = concepts;
    }

    public Boolean getContainsSky() {
        return containsSky;
    }

    public void setContainsSky(Boolean containsSky) {
        this.containsSky = containsSky;
    }

    public Double getRg() {
        return rg;
    }

    public void setRg(Double rg) {
        this.rg = rg;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    @JsonProperty("all_pixels")
    public Integer getAllPixels() {
        return allPixels;
    }

    @JsonProperty("all_pixels")
    public void setAllPixels(Integer allPixels) {
        this.allPixels = allPixels;
    }

    public Double getGb() {
        return gb;
    }

    public void setGb(Double gb) {
        this.gb = gb;
    }

    public Boolean getUsableSky() {
        return usableSky;
    }

    public void setUsableSky(Boolean usableSky) {
        this.usableSky = usableSky;
    }

    @JsonProperty("sky_pixels")
    public Integer getSkyPixels() {
        return skyPixels;
    }

    @JsonProperty("sky_pixels")
    public void setSkyPixels(Integer skyPixels) {
        this.skyPixels = skyPixels;
    }

    @JsonProperty("rg_ratio")
    public Double getRgRatio() {
        return rgRatio;
    }

    @JsonProperty("rg_ratio")
    public void setRgRatio(Double rgRatio) {
        this.rgRatio = rgRatio;
    }

    @JsonProperty("gb_ratio")
    public Double getGbRatio() {
        return gbRatio;
    }

    @JsonProperty("gb_ratio")
    public void setGbRatio(Double gbRatio) {
        this.gbRatio = gbRatio;
    }

}
