package org.n52.sos.hackair.converter;

import java.io.IOException;

import org.junit.Test;
import org.n52.sos.hackair.data.Data;
import org.n52.sos.hackair.data.Response;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class InsertObservationConverterTest extends AbstractConverterTest {

 private InsertObservationConverter converter = new InsertObservationConverter();
    
    @Test
    public void convert() throws JsonParseException, JsonMappingException, IOException, OwsExceptionReport {
        Response response = loadData();
        for (Data data : response.getData()) {
            converter.convert(data);
        }
    }
}
