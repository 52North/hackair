package org.n52.sos.hackair.converter;

import java.io.IOException;

import org.junit.Test;
import org.n52.sos.hackair.data.Data;
import org.n52.sos.hackair.data.Response;
import org.n52.sos.hackair.util.HackAIRHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class InsertSensorConverterTest extends AbstractConverterTest {
    
    private InsertSensorConverter converter = new InsertSensorConverter();
    
    @Test
    public void convert() throws JsonParseException, JsonMappingException, IOException {
        Response response = loadData();
        for (Data data : response.getData()) {
            converter.convert(data.getSourceType(), data.getPollutantQ(), null);
        }
    }
}
