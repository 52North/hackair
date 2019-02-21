
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
public class Concepts {

    private Double sky;
    private Double clouds;
    private Double sun;

    public Double getSky() {
        return sky;
    }

    public void setSky(Double sky) {
        this.sky = sky;
    }

    public Double getClouds() {
        return clouds;
    }

    public void setClouds(Double clouds) {
        this.clouds = clouds;
    }

    public Double getSun() {
        return sun;
    }

    public void setSun(Double sun) {
        this.sun = sun;
    }

}
