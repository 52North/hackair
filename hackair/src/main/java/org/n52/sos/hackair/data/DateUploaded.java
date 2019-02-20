
package org.n52.sos.hackair.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateUploaded {

    @JsonProperty("$date")
    private org.n52.sos.hackair.data.$date $date;

    @JsonProperty("$date")
    public org.n52.sos.hackair.data.$date get$date() {
        return $date;
    }

    @JsonProperty("$date")
    public void set$date(org.n52.sos.hackair.data.$date $date) {
        this.$date = $date;
    }

}
