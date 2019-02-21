
package org.n52.sos.hackair.data;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sensor {

    private Integer id;
    private Integer battery;
    private Integer tamper;
    private Integer error;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean hasId() {
        return getId() != null;
    }

    public Integer getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }

    public Integer getTamper() {
        return tamper;
    }

    public void setTamper(Integer tamper) {
        this.tamper = tamper;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

}
