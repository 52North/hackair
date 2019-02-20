
package org.n52.sos.hackair.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class $date {

    @JsonProperty("$numberLong")
    private String $numberLong;

    @JsonProperty("$numberLong")
    public String get$numberLong() {
        return $numberLong;
    }

    @JsonProperty("$numberLong")
    public void set$numberLong(String $numberLong) {
        this.$numberLong = $numberLong;
    }

}
